package com.example.imvcol;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductosAdapter extends ArrayAdapter<LstItem> implements View.OnClickListener {
    private Context context;
    private ArrayList datos;


    private static class ViewHolder {
        private TextView txtNombre, txtCantidad, txtConteo;
    }

    public ProductosAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.datos = datos;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String dataNombre = getItem(position).getNombre();
        int dataCantidad = getItem(position).getCantidad();
        String dataConteo = getItem(position).getConteo();
        ViewHolder viewHolder;

        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.item, parent, false);

        viewHolder.txtNombre = convertView.findViewById(R.id.item_txt_nombre_producto);
        viewHolder.txtCantidad = convertView.findViewById(R.id.item_txt_cantidad);
        viewHolder.txtConteo = convertView.findViewById(R.id.item_txt_conteo);

        viewHolder.txtNombre.setText(dataNombre);
        viewHolder.txtCantidad.setText(dataCantidad);
        viewHolder.txtNombre.setText(dataConteo);

        return convertView;
    }
}
