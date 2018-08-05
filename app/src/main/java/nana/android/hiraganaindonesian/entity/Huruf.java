package nana.android.hiraganaindonesian.entity;

public class Huruf {

    private int id;
    private String huruf;
    private double value;

    public static final String HURUF_TABLE_NAME = "Huruf";
    public static final String HURUF_COLUMN_ID = "id";
    public static final String HURUF_COLUMN_HURUF = "huruf";
    public static final String HURUF_COLUMN_VALUE = "value";

    public static final String CREATE_TABLE = "CREATE TABLE " + HURUF_TABLE_NAME + "("
            + HURUF_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + HURUF_COLUMN_HURUF + " TEXT,"
            + HURUF_COLUMN_VALUE + " INTEGER"
            + ")";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHuruf() {
        return huruf;
    }

    public void setHuruf(String name) {
        this.huruf = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double age) {
        this.value = age;
    }
}
