package net.foben.schematizer.parse;

import org.openrdf.model.BNode;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.nquads.NQuadsParser;

public class NonNodeStoringNQuadsParser extends NQuadsParser {

	/**
	 * Creates a {@link BNode} object for the specified identifier.
	 */
	protected BNode createBNode(String nodeID)
		throws RDFParseException
	{
		BNode result;	
		if (preserveBNodeIDs()) {
			result = valueFactory.createBNode(nodeID);
		}
		else {
			throw new IllegalArgumentException("This Parser is not supposed to be used without preserving BNode IDs!");
		}

		return result;
	}

}
