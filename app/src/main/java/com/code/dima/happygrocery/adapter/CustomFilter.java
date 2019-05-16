package com.code.dima.happygrocery.adapter;

import android.widget.Filter;

import com.code.dima.happygrocery.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CustomFilter extends Filter{
        private ProductAdapter adapter;
        private List<Product> filterList;

        public CustomFilter(List<Product> filterList, ProductAdapter adapter)
        {
            this.adapter=adapter;
            this.filterList=filterList;
        }
        //FILTERING OCCURS
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results=new FilterResults();
            //CHECK CONSTRAINT VALIDITY
            if(constraint != null && constraint.length() > 0)
            {
                //CHANGE TO UPPER
                constraint=constraint.toString().toUpperCase();
                //STORE OUR FILTERED PLAYERS
                ArrayList<Product> filteredPlayers=new ArrayList<>();
                for (int i=0;i<filterList.size();i++)
                {
                    //CHECK
                    if(filterList.get(i).getName().toUpperCase().contains(constraint))
                    {
                        //ADD PLAYER TO FILTERED PLAYERS
                        filteredPlayers.add(filterList.get(i));
                    }
                }
                results.count=filteredPlayers.size();
                results.values=filteredPlayers;
            }else
            {
                results.count=filterList.size();
                results.values=filterList;
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.set((List<Product>)results.values);
            //REFRESH
            adapter.notifyDataSetChanged();
        }
}
