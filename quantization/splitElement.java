package vector.quantization;

import java.util.ArrayList;

public class splitElement {
    
    Vector value ;
    ArrayList<Vector> assoicated = new ArrayList<>();

    public splitElement() {}

    public splitElement(Vector value ,ArrayList<Vector> assoicated ) {
        this.value = value;
        this.assoicated = assoicated ;
    }

    public Vector getValue() {
        return value;
    }

    public void setValue(Vector value) {
        this.value = value;
    }

    public ArrayList<Vector> getAssoicated() {
        return assoicated;
    }

    public void setAssoicated(ArrayList<Vector> assoicated) {
        this.assoicated = assoicated;
    }
}