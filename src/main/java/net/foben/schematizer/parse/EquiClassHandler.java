package net.foben.schematizer.parse;

import java.io.FileWriter;
import java.io.IOException;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.nquads.NQuadsWriter;

public class EquiClassHandler extends AbstractHandler {

	NQuadsWriter writer;

	public EquiClassHandler(int i) {
		super(i);
		try {
			this.writer = new NQuadsWriter(new FileWriter("equiclasses"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleStatementInternal(Statement st) {
		if (st.getPredicate().stringValue()
				.equals("http://www.w3.org/2002/07/owl#equivalentClass")) {
			if (st.getSubject() instanceof BNode
					|| st.getObject() instanceof BNode) {
				return;
			}
			try {
				writer.handleStatement(st);
			} catch (RDFHandlerException e) {
				e.printStackTrace();
			}
		}

	}

	protected void parseStart() {
		try {
			writer.startRDF();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
	}

	protected void parseEnd() {
		try {
			writer.endRDF();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
	}

}
