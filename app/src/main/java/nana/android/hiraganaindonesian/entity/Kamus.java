package nana.android.hiraganaindonesian.entity;

public class Kamus {

    private long id;
    private String bahasa_jepang;
    private String bahasa_indonesia;

    public Kamus() {

    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setJepang(String bahasa_jepang) {
        this.bahasa_jepang = bahasa_jepang;
    }

    public String getJepang() {
        return bahasa_jepang;
    }

    public void setIndonesia(String bahasa_indonesia) {
        this.bahasa_indonesia = bahasa_indonesia;
    }

    public String getIndonesia() {
        return bahasa_indonesia;
    }


//	@Override
//	public String toString() {
//	    return "Barang "+ nama_barang +" "+ merk_barang + " "+ harga_barang;
//	}
}
