<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="http://www.eclipse.org/default_style.css" type="text/css">
<title>Build Notes for RSE @buildId@</title>
</head>

<body>
<table border="0" cellspacing="5" cellpadding="2" width="100%">
	<tr>
		<td align="LEFT" width="80%">
		<p><b><font class=indextop>Build Notes for RSE @buildId@</font></b><br>
		@dateLong@ </p>
		</td>
	</tr>
</table>
<table border="0" cellspacing="5" cellpadding="2" width="100%">
	<tr>
		<td align="LEFT" valign="TOP" colspan="3" bgcolor="#0080C0"><b>
		<font face="Arial,Helvetica" color="#FFFFFF">New and Noteworthy</font></b></td>
	</tr>
</table>
<table><tbody><tr><td>
<ul>
<li>The <b>CDT Remote Launch Integration</b> is now available as a
  download, or from the <a href="http://download.eclipse.org/dsdp/tm/updates/">Update Site</a>.</li>
<li>An <b>Experimental RSE EFS Provider</b> is now available as a
  download, or from the <a href="http://download.eclipse.org/dsdp/tm/updates/">Update Site</a>.</li>
<li><b>Jakarta Commons Net</b> is now available for <b>FTP</b>.
  A Telnet implementation is available (from Commons Net) but has not yet been integrated 
  as an RSE subsystem.</li>
<li>RSE now has an <b>FTP Only</b> system type (<a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=160168">Bug 160168</a>)</li>
<li>Both Windows dstore daemon and server can now be started by simply double clicking on daemon.bat.
  Documentation has been updated
  (<a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=142952">Bug 142952</a>)</li>
<li>Numerous bugs have been fixed, and we consider RSE safe now for 
  all kinds of data transfer, even if it's done in multiple background sessions.</li>
<li>Use 
  <!-- <a href="https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&classification=DSDP&product=Target+Management&bug_status=RESOLVED&bug_status=VERIFIED&bug_status=CLOSED&resolution=FIXED&resolution=WONTFIX&resolution=INVALID&resolution=WORKSFORME&chfieldfrom=2006-10-09&chfieldto=2006-10-20&chfield=resolution&cmdtype=doit"> -->
  <a href="https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&classification=DSDP&product=Target+Management&target_milestone=1.0+RC2&bug_status=RESOLVED&bug_status=VERIFIED&bug_status=CLOSED&resolution=FIXED&resolution=WONTFIX&resolution=INVALID&resolution=WORKSFORME&cmdtype=doit">
  this query</a> to show the list of bugs fixed since the last milestone,
  <a href="http://download.eclipse.org/dsdp/tm/downloads/drops/S-1.0RC1-200610082121/index.php">
  RSE 1.0RC1</a>.</li>
<li>For details on checkins, see the
  <a href="http://download.eclipse.org/dsdp/tm/downloads/drops/N-changelog/index.html">
  RSE CVS changelog</a>, and the
  <a href="http://download.eclipse.org/dsdp/tm/downloads/drops/N-changelog/core/index.html">
  TM Core CVS changelog</a>.</li>
</ul>
</td></tr></tbody></table>

<table border="0" cellspacing="5" cellpadding="2" width="100%">
	<tr>
		<td align="LEFT" valign="TOP" colspan="3" bgcolor="#808080"><b>
		<font face="Arial,Helvetica" color="#FFFFFF">API Changes since RSE 1.0 M4 (official API freeze)</font></b></td>
	</tr>
</table>
<table><tbody><tr><td>
<ul>
<li><b>Renamed</b> the <b>org.eclipse.rse.ui.subsystemConfiguration</b> extension point 
  to <a href="http://dsdp.eclipse.org/help/latest/topic/org.eclipse.rse.doc.isv/reference/extension-points/org_eclipse_rse_ui_subsystemConfigurations.html">
  subsystemConfigurations</a> 
  in order to better match the standard naming scheme used by the Platform.</li>
<li><b>Renamed</b> the <b>org.eclipse.rse.ui.newConnectionWizardDelegate</b> extension point
  to <a href="http://dsdp.eclipse.org/help/latest/topic/org.eclipse.rse.doc.isv/reference/extension-points/org_eclipse_rse_ui_newConnectionWizardDelegates.html">
  newConnectionWizardDelegates</a> 
  in order to better match the standard naming scheme used by the Platform.</li>
<li><b>Removed</b> the <b>org.eclipse.rse.ui.rseConfigDefaults</b> extension point.
  Use Java Properties instead, as described in the 
  <a href="http://dsdp.eclipse.org/help/latest/index.jsp?topic=/org.eclipse.rse.doc.isv/reference/misc/runtime-options.html">
  runtime-options documentation</a>.</li>
<li><b>Removed</b> the <b>org.eclipse.rse.ui.passwordPersistence</b> extension point.
  The same functionality is achieved by using the data known from the
  IConnectorService implementations of a systemType (supportsUserId(), supportsPassword()).
  As long as a subsystem supports user id and password, the password can also be persisted.
  Persistence is always done case sensitive; Persistence cannot be disabled for system types.</li>
<li><b>Moved</b> several <b>RSE Model Objects and Interfaces</b> from org.eclipse.rse.ui to core:
  <ul>
    <li>(UI) <code>org.eclipse.rse.filters</code> --&gt; <code>org.eclipse.rse.core.filters</code></li>
    <li>(UI) <code>org.eclipse.rse.model</code> --&gt; <code>org.eclipse.rse.core.model</code></li>
    <li>(UI) <code>org.eclipse.rse.references</code> --&gt; <code>org.eclipse.rse.core.references</code></li>
    <li>(UI) <code>org.eclipse.rse.subsystems.servicesubsystem</code> --&gt; <code>org.eclipse.rse.core.subsystems</code></li>
  </ul> 
  Client code can be adapted to the new locations easily by invoking "Organize Imports" except for
  the following additional changes that need to be made:
  <ul>
    <li><b>Event handling methods</b> for <code>ISystemResourceChangeEvent</code>, 
      <code>ISystemPreferenceChangeEvent</code>,
      <code>ISystemModelChangeEvent</code>,
      <code>ISystemRemoteChangeEvent</code> have been removed from 
      <b>ISystemRegistry</b>, such that they are available only in the 
      <b>SystemRegistry</b> implementation. This applies to the fireEvent(),
      postEvent() and corresponding add...Listener() methods. The simplest
      fix in user code is to get the SystemRegistry from RSEUIPlugin 
      instead of SystemRegistry as described below.</li>
    <li>Use <code>RSEUIPlugin.getTheSystemRegistry()</code> instead of <code>SystemRegistry.getSystemRegistry()</code></li>
  </ul>
  Note that wherever possible, client code should only refer to the model object
  interfaces in <code>org.eclipse.rse.core.*</code> and not use the actual 
  implementations which still reside in the UI plugin (these will be moved
  to core eventually, too).
</li>
</ul>
</td></tr></tbody></table>

<table border="0" cellspacing="5" cellpadding="2" width="100%">
	<tr>
		<td align="LEFT" valign="TOP" colspan="3" bgcolor="#0080C0"><b>
		<font face="Arial,Helvetica" color="#FFFFFF">Getting Started</font></b></td>
	</tr>
</table>
<table><tbody><tr><td>
<p>The RSE User Documentation now has a
<a href="http://dsdp.eclipse.org/help/latest/index.jsp?topic=/org.eclipse.rse.doc.user/gettingstarted/g_start.html">
Getting Started Tutorial</a> that guides you through installation, first steps,
connection setup and important tasks.</p>
</td></tr></tbody></table>

<table border="0" cellspacing="5" cellpadding="2" width="100%">
	<tr>
		<td align="LEFT" valign="TOP" colspan="3" bgcolor="#0080C0"><b>
		<font face="Arial,Helvetica" color="#FFFFFF">API Freeze</font></b></td>
	</tr>
</table>
<table><tbody><tr><td>
<p>As per the Target Management 
<a href="http://www.eclipse.org/dsdp/tm/development/plan.php#M4">plan</a>,
we reached API Freeze for RSE M4.</p>
<p>In fact we have reviewed and documented all relevant APIs, but just like most
Eclipse projects, we'll still reserve the right to make API improvements when
committers vote on them. Votes will be held publicly, such that everyone will
be informed in case the APIs should change.</p>
<p>Currently, we see the following areas for potential API changes:
<ul>
  <li>Classes and Interfaces that are not meant for public use will be
   moved to packages tagged as <tt>internal</tt>. This will apply 
   particularly to the "implementation" plugins for the ssh, ftp and
   local subsystems (these do not define any new APIs anyways).</li>
  <li>The <tt>IConnectorService</tt> interface may be slightly modified
   in order to allow for better UI / Non-UI separation.</li>
  <li>Some more RSE Model classes may be moved from the UI plugin to the 
   non-UI core plugin.</li>
</ul>
If you want to start programming against RSE APIs now, best let us know
about your endeavours and keep yourself up-to-date.
</td></tr></tbody></table>

<table border="0" cellspacing="5" cellpadding="2" width="100%">
	<tr>
		<td align="LEFT" valign="TOP" colspan="3" bgcolor="#0080C0"><b>
		<font face="Arial,Helvetica" color="#FFFFFF">Known Problems and Workarounds</font></b></td>
	</tr>
</table>
<table><tbody><tr><td>
The following RC2 <a href="http://www.eclipse.org/dsdp/tm/development/plan.php#M5">plan</a>
deliverables did not make it into this build:
<ul>
<li>User Actions, and Import/Export were deferred with M3 already. 
  A new plan has been published with M3 already.</li>
<li>JUnit tests did not make it into the build due to pending IP legal review.
  They are available from Bugzilla 
  <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=149080">bug 149080</a>
  instead. Due to the missing Unit Test Framework, automated tests could also
  not yet be added to this build.</li>
</ul>
The following critical or major bugs are currently known.
We'll strive to fix these as soon as possible.
<ul>
  <li><a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=143462">bug 143462</a> - maj - [updating] Dirty remote editors do not get notified</li>
  <li><a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=158534">bug 158534</a> - maj - [ssh] Save conflict when modifying remote file</li>
  <li><a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=158765">bug 158765</a> - maj - content assist miss causes enter to not send command</li>
  <li><a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=158766">bug 158766</a> - maj - Content Assist does not work on Windows-local, Linux-local and Windows-dstore shells</li>
  <li><a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=160202">bug 160202</a> - maj -	Remote shell dies.</li>
  <li><a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=160818">bug 160818</a> - maj - Remote Shell view doesn't auto scroll to bottom of text after connect</li>
  <li><a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=161146">bug 161146</a> - maj - downloading a file using FTP inserts spaces between each line</li>
</ul>
<p>The 
<a href="http://wiki.eclipse.org/index.php/RSE_1.0RC2_Known_Issues_and_Workarounds">
RSE 1.0RC2 Known Issues and Workarounds</a> Wiki page gives an up-to-date list
of the most frequent and obvious problems, and describes workarounds for them.
</p>

<p>Click 
<a href="https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&classification=DSDP&product=Target+Management&component=RSE&bug_status=UNCONFIRMED&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&bug_severity=blocker&bug_severity=critical&bug_severity=major&cmdtype=doit">here</a>
for an up-to-date list of major or critical bugs, or
<a href="https://bugs.eclipse.org/bugs/report.cgi?x_axis_field=bug_severity&y_axis_field=op_sys&z_axis_field=&query_format=report-table&classification=DSDP&product=Target+Management&component=RSE&bug_status=UNCONFIRMED&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&format=table&action=wrap">here</a>
for a complete up-to-date bugzilla status report, or
<a href="https://bugs.eclipse.org/bugs/report.cgi?x_axis_field=bug_severity&y_axis_field=op_sys&z_axis_field=&query_format=report-table&classification=DSDP&product=Target+Management&component=RSE&bug_status=RESOLVED&bug_status=VERIFIED&bug_status=CLOSED&format=table&action=wrap">here</a>
for a report on bugs fixed so far.
</p>
</td></tr></tbody></table>

</body>
</html>
