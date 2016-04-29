package boilerride.com.boilerride;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Konstantin on 3/4/2016.
 */
public class FilterDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Select filter")
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        FilterDialogFragmentListener activity = (FilterDialogFragmentListener) getActivity();
                        activity.onFinishFilterDialogFragment("Hi from here");
                        //this.dismiss();
                        //finish();
                        //getActivity().finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });


/*

        LinearLayout LL = new LinearLayout(getActivity());
        LL.setBackgroundColor(Color.CYAN);
        LL.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        LL.setWeightSum(6f);
        LL.setLayoutParams(LLParams);


        ImageView ladder = new ImageView(getActivity());
        ladder.setImageResource(R.drawable.ic_add);

        FrameLayout.LayoutParams ladderParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        ladder.setLayoutParams(ladderParams);

        FrameLayout ladderFL = new FrameLayout(getActivity());
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0);
        ladderFLParams.weight = 5f;
        ladderFL.setLayoutParams(ladderFLParams);
        ladderFL.setBackgroundColor(Color.GREEN);
        View dummyView = new View(getActivity());

        LinearLayout.LayoutParams dummyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
        dummyParams.weight = 1f;
        dummyView.setLayoutParams(dummyParams);
        dummyView.setBackgroundColor(Color.RED);
        //View view=getActivity().findViewById(R.id.dialog_linlay);
        builder.setView(LL);

*/


        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface FilterDialogFragmentListener {
        void onFinishFilterDialogFragment(String inputText);
    }



}