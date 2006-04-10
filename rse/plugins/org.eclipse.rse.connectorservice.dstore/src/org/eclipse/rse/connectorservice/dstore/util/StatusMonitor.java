/********************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * The following IBM employees contributed to the Remote System Explorer
 * component that contains this file: David McKnight, Kushal Munir, 
 * Michael Berger, David Dykstal, Phil Coulthard, Don Yantzi, Eric Simpson, 
 * Emily Bruner, Mazen Faraj, Adrian Storisteanu, Li Ding, and Kent Hawley.
 * 
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.rse.connectorservice.dstore.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dstore.core.model.DE;
import org.eclipse.dstore.core.model.DataElement;
import org.eclipse.dstore.core.model.DataStore;
import org.eclipse.dstore.extra.internal.extra.DomainEvent;
import org.eclipse.dstore.extra.internal.extra.IDomainListener;
import org.eclipse.rse.core.SystemBasePlugin;
import org.eclipse.rse.core.SystemPlugin;
import org.eclipse.rse.core.subsystems.CommunicationsEvent;
import org.eclipse.rse.core.subsystems.ICommunicationsListener;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/*
 * This utility class can be used to monitor the status of one more more status DataElements.
 * Only one instanceof of this class is required per DataStore for use in monitoring statuses.
 * This is intended to be used in place of StatusChangeListeners
 * 
 *  * <p>
 * The following is one example of the use of the StatusMonitor. The code:
 * <blockquote><pre>
 *    		DataElement status = dataStore.command(dsCmd, args, deObj);
 *
 *			StatusMonitor smon = StatusMonitorFactory.getInstance().getStatusMonitorFor(getSystem(), ds);
 *			smon.waitForUpdate(status, monitor);
 * </pre></blockquote>
 */
public class StatusMonitor implements IDomainListener, ICommunicationsListener 
{

	protected Shell _shell;
	protected IConnectorService _system;

	
	protected boolean _networkDown = false;
	
	protected List _workingStatuses;
	protected List _cancelledStatuses;
	protected List _doneStatuses;
	
	protected DataStore _dataStore;

	protected class FindShell implements Runnable 
	{
		private Shell shell;
		
		/**
		 * @see Runnable#run()
		 */
		public void run() 
		{
			try {
				Shell[] shells = Display.getCurrent().getShells();
				for (int loop = 0; loop < shells.length && shell == null; loop++) {
					if (shells[loop].isEnabled()) {
						shell = shells[loop];
					}
				}
			} catch (Exception e) {
				SystemBasePlugin.logError("StatusChangeListener.FindShell exception: ", e);
			}
		}
	}

	/**
	 * Construct a StatusChangeListener
	 *
	 * @param system the system associated with this monitor
	 * @param dataStore the dataStore associated with this monitor
	 * @param factory the diagnostic factory for this monitor
	 */	
	public StatusMonitor(IConnectorService system, DataStore dataStore, ICommunicationsDiagnosticFactory factory) 
	{
		_system = system;
		_dataStore = dataStore;
		reInit();
	}
	
	/**
	 * Construct a StatusChangeListener
	 *
	 * @param system the system associated with this monitor
	 * @param dataStore the dataStore associated with this monitor
	 */	
	public StatusMonitor(IConnectorService system, DataStore dataStore) 
	{
	    this(system, dataStore, null);
	}
	
	public void reInit()
	{
		_networkDown = false;
		_system.addCommunicationsListener(this);		
		_workingStatuses = new ArrayList();
		_doneStatuses = new ArrayList();
		_cancelledStatuses = new ArrayList();
		_dataStore.getDomainNotifier().addDomainListener(this);
	}
	
	public DataStore getDataStore()
	{
	    return _dataStore;
	}
	
	public void dispose()
	{
	    _system.removeCommunicationsListener(this);
	    _workingStatuses.clear();
	    _doneStatuses.clear();
	    _cancelledStatuses.clear();
	    _dataStore.getDomainNotifier().removeDomainListener(this);
	}
	
	/**
	 * @see IDomainListener#listeningTo(DomainEvent)
	 */
	public boolean listeningTo(DomainEvent event) 
	{
		if (_workingStatuses.size() == 0)
		{
			return true;
		}

		DataElement parent = (DataElement)event.getParent();
		if (_workingStatuses.contains(parent))
		{
		    return determineStatusDone(parent);
		}
		
		return false;
	}
	


	/**
	 * @see IDomainListener#domainChanged(DomainEvent)
	 */
	public void domainChanged(DomainEvent event) 
	{
	    if (_workingStatuses.size() == 0)
		{
			return;
		}

		DataElement parent = (DataElement)event.getParent();
		if (_workingStatuses.contains(parent))
		{
		    boolean isStatusDone = determineStatusDone(parent);
		    if (isStatusDone)
		    {
		        setDone(parent);
		    }
		}
	}
	
    
    /**
     * Determines whether the status is done.
     * @return <code>true</code> if status done, <code>false</code> otherwise.
     */
    protected boolean determineStatusDone(DataElement status) 
    {        
        return status.getAttribute(DE.A_VALUE).equals("done") ||  status.getAttribute(DE.A_NAME).equals("done");
    }
	
	/**
	 * @see org.eclipse.rse.core.subsystems.ICommunicationsListener#isPassiveCommunicationsListener()
	 */
	public boolean isPassiveCommunicationsListener() 
	{
		return false;
	}

	/**
	 * setDone(boolean)
	 */
	public synchronized void setDone(DataElement status)
	{	
	    _workingStatuses.remove(status);
	    _doneStatuses.add(status);	   
	}
	
	
	public synchronized void setCancelled(DataElement status)
	{
	    _workingStatuses.remove(status);
	    _cancelledStatuses.add(status);
	}
	
	public synchronized void setWorking(DataElement status)
	{
	    _workingStatuses.add(status);
	}
	
	
	public boolean wasCancelled(DataElement status) 
	{
	    if (_cancelledStatuses.contains(status))
	    {
	        return true;
	    }
		return false;
	}
	
	/**
	 * @see IDomainListener#getShell()
	 */
	public Shell getShell() 
	{
		// dy:  DomainNotifier (which calls this method) requires the shell not be disposed
		//if (shell == null) {
		if (_shell == null || _shell.isDisposed())
		{
			FindShell findShell = new FindShell();
			Display.getDefault().syncExec(findShell);
			_shell = findShell.shell;
		}
		return _shell;
	}

	/**
	 * @see ICommunicationsListener#communicationsStateChange(CommunicationsEvent)
	 */
	public void communicationsStateChange(CommunicationsEvent e) 
	{
		if (e.getState() == CommunicationsEvent.CONNECTION_ERROR) 
		{
			_networkDown = true;
		}
		else if (e.getState() == CommunicationsEvent.AFTER_DISCONNECT)
		{
			_networkDown = true;
		}
	}

	/**
	 * Test if the StatusChangeListener returned because the network connection to the 
	 * remote system was broken.
	 */
	public boolean isNetworkDown() 
	{
		return _networkDown;
	}
	
	 public DataElement waitForUpdate(DataElement status) throws InterruptedException
		{	
	        return waitForUpdate(status, null, 0);
		}
	
    public DataElement waitForUpdate(DataElement status, IProgressMonitor monitor) throws InterruptedException
	{	
        return waitForUpdate(status, monitor, 0);
	}
    
    public DataElement waitForUpdate(DataElement status, int wait) throws InterruptedException
	{	
        return waitForUpdate(status, null, wait);
	} 
    
    public synchronized DataElement waitForUpdate(DataElement status, IProgressMonitor monitor, int wait) throws InterruptedException
	{	
    	if (_networkDown && status.getDataStore().isConnected())
    	{
    		reInit();
    	}
        if (determineStatusDone(status))
        {
            setDone(status);
            return status;
        }
        
        setWorking(status);
     
		Display display = Display.getCurrent();
				
	  // Prevent infinite looping by introducing a threshold for wait 
      int WaitThreshold = 600; //default. sleep(100ms) for 600 times  		
      if ( wait > 0 )
        WaitThreshold = wait*10; // 1 second means 10 sleep(100ms)
      else if ( wait == -1 ) // force a diagnostic
	  		 WaitThreshold = -1;
         
      if (display != null) 
		{
			// Current thread is UI thread
			while (_workingStatuses.contains(status)) 
			{
				// Process everything on event queue
				while (display.readAndDispatch()) {}
				
				if ((monitor != null) && (monitor.isCanceled())) 
				{
				    setCancelled(status);
					throw new InterruptedException();
				}
				
				boolean statusDone = determineStatusDone(status);
				
				if (statusDone)
				{
					setDone(status);
				}
				else
				{
				    Thread.sleep(100);

				    if (WaitThreshold > 0) // update timer count if
                        // threshold not reached
                        --WaitThreshold; // decrement the timer count
                   
				  if (WaitThreshold == 0)
				    {
				        // no diagnostic factory but there is a timeout
				        return status;  // returning the undone status object
				    }
                    else if (_networkDown)
                    {
                        dispose();
    					throw new InterruptedException();
                    }
                }
			}
			
		} 
		else 
		{
			// Current thread is not UI thread
			while (_workingStatuses.contains(status))
			{
				
			    if ((monitor != null) && (monitor.isCanceled()))
			    {
					setCancelled(status);
					throw new InterruptedException();
				}

				boolean statusDone = determineStatusDone(status);
				
				if (statusDone)
				{
					setDone(status);
				}
				else 
				{
                    Thread.sleep(100);
                    
                    if (WaitThreshold > 0) // update timer count if
                        // threshold not reached
                        --WaitThreshold; // decrement the timer count

                   if (WaitThreshold == 0)
				    {
				        // no diagnostic factory but there is a timeout
				        return status;  // returning the undone status object
				    }
                    else if (_networkDown)
                    {
    					dispose();
    					throw new InterruptedException();
                    }
                }
			}		
		}

	
		return status;
	}


    /**
     * Start diagnostic 
     *
     * @param Class diagnostic is the an implementation of ICommunicationsDiagnostic
     * @param boolean quiet is the flag to indicate if user should be prompted
     *         - true for no prompt
     * @return ICommunciationsDiagnostic class instance
     */
    public ICommunicationsDiagnostic whatIsGoingOn(ICommunicationsDiagnosticFactory factory, boolean quiet, DataElement target) throws InterruptedException  //@01
    {
    	if (target == null)
    	   return null;
    	
    	ICommunicationsDiagnostic d = null;
		try {
				 String name = target.getName();  /* Get the current element status name: started/working/done */
				 /* Log the current status */
				 SystemBasePlugin.logError("StatusChangeListener."+name+": " + "Communications Diagnostic started");			
		    	 SystemBasePlugin.logError("StatusChangeListener."+name +
		    	                           ": done = " + _doneStatuses.contains(target) +
		                                   "; cancelled = " + _cancelledStatuses.contains(target)+
		                                   "; _networkDown = " + _networkDown );
		
		         DataStore ds = _dataStore;
		         /* Log the status in DataStore */
		         SystemBasePlugin.logError("StatusChangeListener."+name+"(DataStore): " + " isConnected = " + ds.isConnected() + "; isWaiting = " + ds.isWaiting(target));
		         		
		         /*Log all nested DataElement's in target's parent*/
		         List deList = target.getParent().getNestedData();
		         if ( deList != null && !deList.isEmpty() ) {
		         	 int num = deList.size();
		             for ( int i = 0; i < num; i++)
		             {
		                 DataElement child = (DataElement) deList.get(i);
		                 if (child != null) {
		                 	SystemBasePlugin.logError("StatusChangeListener."+name+".child"+i+"(DataElement): " + child.toString());
		                 	DataElement descriptor = child.getDescriptor();	
		                 	if (descriptor != null)
		                 		SystemBasePlugin.logError("StatusChangeListener."+name+".child"+i+"(Descriptor):  " + descriptor.toString());
		                 }
		             }
		         }	
		         //Spawn off a diagnostic to check more stuff
		         if (factory != null) {
		            d = factory.createInstance();
		
		            //Initialize the diagnostic instance
		            //Set diagnostic id(name), server name(ds.getName()), this connection(system)
		            d.setUp(name, quiet, ds.getName(),_system, null ,null, null);
		         	
		            new Thread(d).start();
		         }
		
		}
		catch (Exception e)
		{
		         SystemBasePlugin.logError("StatusChangeListener.ICommunicationsDiagnostic exception: ", e);  	
		}
	
	    return d;	// return the created diagnostic class instance	
    }


}