/**
 * 
 */
package mwn.bolfab.wakeupbuddy;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


/**
 * @author ogunjobj
 * description - This class extends the ArrayAdapter class and allows for the listview to be displayed with checkboxes.
 * It also reads the selected options so that they can be stored.
 */
public class CheckboxAdapter extends ArrayAdapter<String> {
	private LayoutInflater mInflater;

    private String[] mStrings;
    //private TypedArray mIcons;
    private int mViewResourceId;

    static ArrayList<String> selectedStrings = new ArrayList<String>();

	public CheckboxAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mStrings = objects;

        mViewResourceId = resource;

	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mStrings.length;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		
		return mStrings[position];
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static ArrayList<String> getSelectedString(){
		  return selectedStrings;
		}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = mInflater.inflate(mViewResourceId, null);
		final CheckBox tv = (CheckBox)convertView.findViewById(R.id.checkbox1);
        tv.setText(mStrings[position]);
        
        tv.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedStrings.add(tv.getText().toString());
                }else{
                    selectedStrings.remove(tv.getText().toString());
                }

            }
        });
        return convertView;

	}

}
