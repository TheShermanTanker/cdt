/*
 *(c) Copyright QNX Software Systems Ltd. 2002.
 * All Rights Reserved.
 * 
 */
package org.eclipse.cdt.debug.internal.ui.editors;

import org.eclipse.cdt.debug.ui.CDebugUIPlugin;
import org.eclipse.cdt.internal.ui.editor.asm.AsmTextEditor;
import org.eclipse.cdt.ui.CUIPlugin;

/**
 * Enter type comment.
 * 
 * @since: Jan 6, 2003
 */
public class DisassemblyEditor extends AsmTextEditor
{
	static final public String DISASSEMBLY_EDITOR_ID = "org.eclipse.cdt.debug.ui.DisassemblyEditor"; //$NON-NLS-1$

	/**
	 * Constructor for DisassemblyEditor.
	 */
	public DisassemblyEditor()
	{
		super();
		setDocumentProvider( CDebugUIPlugin.getDefault().getDisassemblyDocumentProvider() );
		setSourceViewerConfiguration( new DisassemblySourceViewerConfiguration( CUIPlugin.getDefault().getAsmTextTools(), this ) );
		setEditorContextMenuId("#DisassemblyEditorContext"); //$NON-NLS-1$
		setRulerContextMenuId("#DisassemblyEditorRulerContext"); //$NON-NLS-1$
	}
}
