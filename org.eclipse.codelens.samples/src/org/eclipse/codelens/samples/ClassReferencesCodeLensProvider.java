package org.eclipse.codelens.samples;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.provisional.codelens.Command;
import org.eclipse.jface.text.provisional.codelens.ICodeLens;
import org.eclipse.jface.text.provisional.codelens.ICodeLensContext;
import org.eclipse.jface.text.provisional.codelens.ICodeLensProvider;

public class ClassReferencesCodeLensProvider implements ICodeLensProvider {

	@Override
	public ICodeLens[] provideCodeLenses(ICodeLensContext context, IProgressMonitor monitor) {
		ITextViewer textViewer = context.getViewer();
		IDocument document = textViewer.getDocument();
		List<ICodeLens> lenses = new ArrayList<>();
		int lineCount = document.getNumberOfLines();
		for (int i = 0; i < lineCount; i++) {
			String line = getLineText(document, i, false);
			int index = line.indexOf("class ");
			if (index != -1) {
				String className = line.substring(index + "class ".length(), line.length());
				index = className.indexOf(" ");
				if (index != -1) {
					className = className.substring(0, index);
				}
				if (className.length() > 0) {
					lenses.add(new ClassCodeLens(className, i + 1));
				}
			}
		}
		return lenses.toArray(new ICodeLens[0]);
	}

	@Override
	public ICodeLens resolveCodeLens(ICodeLensContext context, ICodeLens codeLens, IProgressMonitor monitor) {
		ITextViewer textViewer = context.getViewer();
		IDocument document = textViewer.getDocument();
		String className = ((ClassCodeLens) codeLens).getClassName();
		int refCount = 0;
		int lineCount = document.getNumberOfLines();
		for (int i = 0; i < lineCount; i++) {
			String line = getLineText(document, i, false);
			refCount += line.contains("new " + className) ? 1 : 0;
		}
		((ClassCodeLens) codeLens).setCommand(new Command(refCount + " references", ""));
		return codeLens;
	}

	private static String getLineText(IDocument document, int line, boolean withLineDelimiter) {
		try {
			int lo = document.getLineOffset(line);
			int ll = document.getLineLength(line);
			if (!withLineDelimiter) {
				String delim = document.getLineDelimiter(line);
				ll = ll - (delim != null ? delim.length() : 0);
			}
			return document.get(lo, ll);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
