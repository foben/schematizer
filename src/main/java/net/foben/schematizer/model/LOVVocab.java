package net.foben.schematizer.model;

public class LOVVocab {

	private final String prefix;
	private final String namespace;
	private final String description;

	public LOVVocab(String prefix, String namespace, String description) {
		this.prefix = prefix;
		this.namespace = namespace;
		this.description = description;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getDescription() {
		return description;
	}

	public String toString() {
		return "{" + prefix + "}";
	}

}
