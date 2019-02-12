package lossylzwcompression;


public class Tuple {

    private final Object[] elements;

    private Tuple(int size) {
        elements = new Object[size];
    }

    public Tuple(Object firstElement, Object... elements) {
        this.elements = new Object[elements.length + 1];
        this.elements[0] = firstElement;
        System.arraycopy(elements, 0, this.elements, 1, elements.length);
    }

    @Override
    public Tuple clone() {
        Tuple ret = new Tuple(elements.length);
        System.arraycopy(elements, 0, ret.elements, 0, elements.length);
        return ret;
    }

    public Object getElement(int idx) {
        return elements[idx];
    }

    public int getSize() {
        return elements.length;
    }

    @Override
    public String toString() {
        String toReturn = "Tuple(";
        for (int i = 0; i < elements.length; i++) {
            if (i != 0) {
                toReturn += ",";
            }
            toReturn += elements[i].toString();
        }
        toReturn += ")";
        return toReturn;
    }

    @Override
    public boolean equals(Object anObject) {
        try {
            if (((Tuple) anObject).getSize() != getSize()) {
                return false;
            }
            for (int i = 0; i < getSize(); i++) {
                if (!((Tuple) anObject).getElement(i).equals(getElement(i))) {
                    return false;
                }
            }
            return true;
        } catch (java.lang.ClassCastException e) {
            return false;
        }
    }
}
