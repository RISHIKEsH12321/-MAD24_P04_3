//package sg.edu.np.mad.travelhub;
//
//import android.content.Context;
//import android.graphics.Typeface;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseExpandableListAdapter;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class ExpandableListAdapter extends BaseExpandableListAdapter {
//    private Context context;
//    private List<String> listTitle;
//    private HashMap<String, List<String>> listDetail;
//
//    public ExpandableListAdapter(Context context, List<String> listTitle, HashMap<String, List<String>> listDetail){
//        this.context = context;
//        this.listTitle = listTitle;
//        this.listDetail = listDetail;
//    }
//
//    @Override
//    public Object getChild(int groupPosition, int childPosition) {
//        return this.listDetail.get(this.listTitle.get(groupPosition))
//                .get(childPosition);
//    }
//
//    @Override
//    public long getChildId(int groupPosition, int childPosition) {
//        return childPosition;
//    }
//
//    @Override
//    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        final String listText = (String) getChild(groupPosition, childPosition);
//        if (convertView == null){
//            LayoutInflater layoutInflater = (LayoutInflater) this.context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.list_item, null);
//        }
//        TextView listTextView = (TextView) convertView.findViewById(R.id.expandedListItem);
//        listTextView.setText(listText);
//        return convertView;
//    }
//
//    @Override
//    public int getChildrenCount(int groupPosition) {
//        return this.listDetail.get(this.listTitle.get(groupPosition)).size();
//    }
//
//    @Override
//    public Object getGroup(int groupPosition) {
//        return this.listTitle.get(groupPosition);
//    }
//
//    @Override
//    public int getGroupCount() {
//        return this.listTitle.size();
//    }
//
//    @Override
//    public long getGroupId(int groupPosition) {
//        return groupPosition;
//    }
//
//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        String listTitle = (String) getGroup(groupPosition);
//        if (convertView == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) this.context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.list_group, null);
//        }
//        TextView listTitleTextView = (TextView) convertView
//                .findViewById(R.id.listTitle);
//        listTitleTextView.setTypeface(null, Typeface.BOLD);
//        listTitleTextView.setText(listTitle);
//        return convertView;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return false;
//    }
//
//
//    @Override
//    public boolean isChildSelectable(int groupPosition, int childPosition) {
//        return true;
//    }
//
//}