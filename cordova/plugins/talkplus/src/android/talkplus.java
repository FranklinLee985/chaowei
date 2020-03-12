package org.apache.cordova.talkplus;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
/**
 * This class echoes a string called from JavaScript.
 */
public class talkplus extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("showToast")) {
            String message = args.getString(0);
            this.showToast(message, callbackContext);
            return true;
        }
		else if (action.equals("joinRoom")) {
            String message = args.getString(0);
            this.joinRoom(message, callbackContext);
            return true;
        }
        return false;
    }

    private void showToast(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
			Toast.makeText(cordova.getContext(),message,Toast.LENGTH_SHORT).show();
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
	
	private void joinRoom(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
			//Toast.makeText(cordova.getContext(),message,Toast.LENGTH_SHORT).show();
			Toast.makeText(cordova.getContext(),message,Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(message));
		   //Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("chaoweiclass://?host=global.talk-cloud.net&password=5678&serial=545718172&nickname=kenny&userrole=2"));
           cordova.getActivity().startActivity(intent);

            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
	
}
