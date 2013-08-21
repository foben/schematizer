package net.foben.schematizer.parse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.foben.schematizer.model.ModelAccess;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import com.google.common.collect.HashBasedTable;

public class LOVStatisticExtractHandler extends AbstractHandler {

    Set<String> lovnamespaces;
    HashBasedTable<String, String, Integer> datatable;

    public LOVStatisticExtractHandler(int i) {
	super(i);
	this.lovnamespaces = ModelAccess.getLOVNamespaces();
	this.datatable = HashBasedTable.create(840, this.lovnamespaces.size());
    }

    @Override
    public void handleStatementInternal(Statement st) {
	String context = st.getContext().stringValue();
	increase(context, "TOTAL");

	boolean checkObj = (st.getObject() instanceof URI);
	boolean checkPred = true;
	String pred = st.getPredicate().stringValue();
	String obj = checkObj ? st.getObject().stringValue() : "";
	Set<String> incSet = new HashSet<String>();
	for (String vocab : lovnamespaces) {
	    if (checkPred) {
		if (pred.startsWith(vocab)) {
		    incSet.add(vocab);
		    checkPred = false;
		}
	    }
	    if (checkObj) {
		if (obj.startsWith(vocab)) {
		    incSet.add(vocab);
		    checkObj = false;
		}
	    }
	    if (!checkPred && !checkObj)
		break;
	}
	for (String vocab : incSet) {
	    increase(context, vocab);
	}

	// String predNs = st.getPredicate().getNamespace();
	// if (lovnamespaces.contains(predNs)) {
	// increase(context, predNs);
	// }
	// if (st.getObject() instanceof URI) {
	// String objNs = ((URI) st.getObject()).getNamespace();
	// if (lovnamespaces.contains(objNs)) {
	// increase(context, objNs);
	// }
	// }
    }

    private void increase(String context, String column) {
	if (datatable.contains(context, column)) {
	    int val = datatable.get(context, column);
	    val++;
	    datatable.put(context, column, val);
	} else {
	    datatable.put(context, column, 1);
	}
    }

    protected void parseEnd() {
	String del = ";";
	try {
	    BufferedWriter br = new BufferedWriter(new FileWriter("temp/lovoccurencetable.csv"));

	    for (String col : datatable.columnKeySet()) {
		br.write(del + col);
	    }
	    br.newLine();
	    for (String row : datatable.rowKeySet()) {
		br.write(row);
		for (String col : datatable.columnKeySet()) {
		    Integer value = datatable.get(row, col);
		    br.write(del + (value == null ? 0 : value));
		}
		br.newLine();
	    }
	    br.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

}
