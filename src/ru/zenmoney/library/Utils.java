package ru.chedim;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import ru.zenmoney.library.R;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 23:33
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    public static void alert(Context context, String title, String text) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(text);
        alertDialog.show();
    }

    public static boolean confirm(Context context, int title, int text, final ConfirmListener h) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(context.getString(text));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                h.onYes();
            }
        });
        alertDialog.setButton2(context.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                h.onNo();
            }
        });
        alertDialog.show();
        return false;
    }

    public static void message(Context context, int message) {
        Toast toast = Toast.makeText(context, message, 2000);
        toast.show();
    }

    public static void message(Context context, CharSequence message) {
        Toast toast = Toast.makeText(context, message, 2000);
        toast.show();
    }

    public static void alert(Context context, int title, int text) {
        alert(context, context.getString(title), context.getString(text));
    }

    public abstract interface ConfirmListener {
        public void onYes();
        public void onNo();
    }

}
