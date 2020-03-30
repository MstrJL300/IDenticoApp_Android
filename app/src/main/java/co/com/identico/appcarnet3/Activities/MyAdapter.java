package co.com.identico.appcarnet3.Activities;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import co.com.identico.appcarnet3.Models.Carnet;
import co.com.identico.appcarnet3.R;

public class MyAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Carnet> names;

    public MyAdapter(Context context, int layout, List<Carnet> names){
        this.context = context;
        this.layout = layout;
        this.names = names;
    }

    @Override
    public int getCount() {
        return this.names.size();
    }

    @Override
    public Object getItem(int position) {
        return this.names.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Copiamos la vista
        View v = convertView;

        //inflamos la vista que nos a llegado con nuestro layout personalizado
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        v= layoutInflater.inflate( R.layout.list_item,null);

        // Nos traemosel valor actual dependiente de la posicion
        String currentName = names.get(position).getIdenticoNombreCliente()+
                "\r\n"+names.get(position).getIdenticoNombreCarnet();

        //Referenciamos e elmento a modificar y lo rellenamos
        TextView textView = (TextView) v.findViewById(R.id.textView);
        textView.setText(currentName);

        //devolvemos la vista inflada y modificada con nuestros datos
        return v;
    }
}
