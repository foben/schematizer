package net.foben.schematizer.util;

/*
 * Tuple implementation 
 */
public class ThreeTuple<X, Y, Z> {
    public final X x;
    public final Y y;
    public final Z z;

    public ThreeTuple(X x, Y y, Z z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public String toString() {
	return String.format("(%s, %s, %s)", x, y, z);
    }

}
