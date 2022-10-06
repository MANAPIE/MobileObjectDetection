package me.manapie.objectdetection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private ArrayList<ObjectItem> mObjectList;

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        holder.onBind(mObjectList.get(position));
    }

    public void setObjectList(ArrayList<ObjectItem> list) {
        this.mObjectList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mObjectList == null)
            return 0;
        return mObjectList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView objectClass;
        TextView objectPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            objectClass = (TextView) itemView.findViewById(R.id.object_class);
            objectPosition = (TextView) itemView.findViewById(R.id.object_position);
        }

        void onBind(ObjectItem item) {
            objectClass.setText(item.getObjectClass());
            objectPosition.setText(item.getObjectPosition());
        }
    }
}