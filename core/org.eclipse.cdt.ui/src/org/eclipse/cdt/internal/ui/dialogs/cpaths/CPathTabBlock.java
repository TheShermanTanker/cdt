/*******************************************************************************
 * Copyright (c) 2004 QNX Software Systems and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: QNX Software Systems - initial API and implementation
 ******************************************************************************/
package org.eclipse.cdt.internal.ui.dialogs.cpaths;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IPathEntry;
import org.eclipse.cdt.internal.ui.dialogs.IStatusChangeListener;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CPathTabBlock extends AbstractPathOptionBlock {

	private int[] pathTypes = { IPathEntry.CDT_SOURCE, IPathEntry.CDT_PROJECT, IPathEntry.CDT_OUTPUT, IPathEntry.CDT_LIBRARY,
			IPathEntry.CDT_CONTAINER};
	private CheckedListDialogField fCPathList;

	private CPathSourceEntryPage fSourcePage;
	private CPathProjectsEntryPage fProjectsPage;
	private CPathOutputEntryPage fOutputPage;
	private CPathContainerEntryPage fContainerPage;
	private CPathLibraryEntryPage fLibrariesPage;

	private class BuildPathAdapter implements IDialogFieldListener {

		// ---------- IDialogFieldListener --------
		public void dialogFieldChanged(DialogField field) {
			buildPathDialogFieldChanged(field);
		}
	}

	void buildPathDialogFieldChanged(DialogField field) {
		if (field == fCPathList) {
			updateCPathStatus();
		}
		doStatusLineUpdate();
	}

	public CPathTabBlock(IStatusChangeListener context, int pageToShow) {
		super(context, pageToShow);

		String[] buttonLabels = new String[] { /* 0 */CPathEntryMessages.getString("CPathsBlock.path.up.button"), //$NON-NLS-1$
				/* 1 */CPathEntryMessages.getString("CPathsBlock.path.down.button"), //$NON-NLS-1$
				/* 2 */null, /* 3 */CPathEntryMessages.getString("CPathsBlock.path.checkall.button"), //$NON-NLS-1$
				/* 4 */CPathEntryMessages.getString("CPathsBlock.path.uncheckall.button") //$NON-NLS-1$

		};
		BuildPathAdapter adapter = new BuildPathAdapter();

		fCPathList = new CheckedListDialogField(null, buttonLabels, new CPElementLabelProvider());
		fCPathList.setDialogFieldListener(adapter);
		fCPathList.setLabelText(CPathEntryMessages.getString("CPathsBlock.path.label")); //$NON-NLS-1$
		fCPathList.setUpButtonIndex(0);
		fCPathList.setDownButtonIndex(1);
		fCPathList.setCheckAllButtonIndex(3);
		fCPathList.setUncheckAllButtonIndex(4);

	}

	protected List getCPaths() {
		return fCPathList.getElements();
	}

	protected void addTabs() {
		fSourcePage = new CPathSourceEntryPage(fCPathList);
		addPage(fSourcePage);
		fOutputPage = new CPathOutputEntryPage(fCPathList);
		addPage(fOutputPage);
		fProjectsPage = new CPathProjectsEntryPage(fCPathList);
		addPage(fProjectsPage);
		fLibrariesPage = new CPathLibraryEntryPage(fCPathList);
		addPage(fLibrariesPage);
		fContainerPage = new CPathContainerEntryPage(fCPathList);
		addPage(fContainerPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.ui.dialogs.TabFolderOptionBlock#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		if (getCProject() != null) {
			fSourcePage.init(getCProject());
			fOutputPage.init(getCProject());
			fProjectsPage.init(getCProject());
			fContainerPage.init(getCProject());
			fLibrariesPage.init(getCProject());
		}
		Dialog.applyDialogFont(control);
		return control;
	}


	protected void initialize(ICElement element, List cPaths) {

		List exportedEntries = new ArrayList();
		for (int i = 0; i < cPaths.size(); i++) {
			CPElement curr = (CPElement) cPaths.get(i);
			if (curr.isExported() && curr.getEntryKind() != IPathEntry.CDT_SOURCE) {
				exportedEntries.add(curr);
			}
		}

		fCPathList.setElements(cPaths);
		fCPathList.setCheckedElements(exportedEntries);

		if (fProjectsPage != null) {
			fSourcePage.init(getCProject());
			fOutputPage.init(getCProject());
			fProjectsPage.init(getCProject());
			fContainerPage.init(getCProject());
			fLibrariesPage.init(getCProject());
		}

		doStatusLineUpdate();
		initializeTimeStamps();
	}

	
	protected int[] getFilteredTypes() {
		return pathTypes;
	}
	
	/**
	 * Validates the build path.
	 */
	public void updateCPathStatus() {
		getPathStatus().setOK();

		List elements = fCPathList.getElements();

		CPElement entryMissing = null;
		int nEntriesMissing = 0;
		IPathEntry[] entries = new IPathEntry[elements.size()];

		for (int i = elements.size() - 1; i >= 0; i--) {
			CPElement currElement = (CPElement) elements.get(i);
			boolean isChecked = fCPathList.isChecked(currElement);
			if (currElement.getEntryKind() == IPathEntry.CDT_SOURCE) {
				if (isChecked) {
					fCPathList.setCheckedWithoutUpdate(currElement, false);
				}
			} else {
				currElement.setExported(isChecked);
			}

			entries[i] = currElement.getPathEntry();
			if (currElement.isMissing()) {
				nEntriesMissing++;
				if (entryMissing == null) {
					entryMissing = currElement;
				}
			}
		}

		if (nEntriesMissing > 0) {
			if (nEntriesMissing == 1) {
				getPathStatus().setWarning(CPathEntryMessages.getFormattedString("CPathsBlock.warning.EntryMissing", //$NON-NLS-1$
						entryMissing.getPath().toString()));
			} else {
				getPathStatus().setWarning(CPathEntryMessages.getFormattedString("CPathsBlock.warning.EntriesMissing", //$NON-NLS-1$
						String.valueOf(nEntriesMissing)));
			}
		}

		/*
		 * if (fCurrJProject.hasClasspathCycle(entries)) {
		 * fClassPathStatus.setWarning(NewWizardMessages.getString("BuildPathsBlock.warning.CycleInClassPath"));
		 * //$NON-NLS-1$ }
		 */
		updateBuildPathStatus();
	}
}