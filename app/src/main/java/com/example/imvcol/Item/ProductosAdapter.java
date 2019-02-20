package com.example.imvcol.Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.imvcol.R;

import java.util.ArrayList;

public class ProductosAdapter extends ArrayAdapter<LstItem> implements View.OnClickListener {
    private ArrayList<LstItem> dataSet;
    private Context context;


    private static class ViewHolder {
        private TextView txtNombre, txtCantidad, txtConteo;
    }

    public ProductosAdapter(ArrayList<LstItem> datos, Context context) {
        super(context, R.layout.list_view_item, datos);
        this.context = context;
        this.dataSet = datos;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_view_item, parent, false);

            viewHolder.txtNombre = convertView.findViewById(R.id.item_txt_nombre_producto);
            viewHolder.txtCantidad = convertView.findViewById(R.id.item_txt_cantidad);
            viewHolder.txtConteo = convertView.findViewById(R.id.item_txt_conteo);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtNombre.setText(getItem(position).getNombre());
        viewHolder.txtCantidad.setText("Stock: " + String.valueOf(getItem(position).getCantidad()));
        viewHolder.txtConteo.setText("Conteo: " + String.valueOf(getItem(position).getConteo()));


        return convertView;
    }
}
