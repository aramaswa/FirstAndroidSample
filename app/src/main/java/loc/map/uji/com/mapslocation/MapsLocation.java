package loc.map.uji.com.mapslocation;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MapsLocation extends AppCompatActivity {

    private static final int PICK_FIRST_CONTACT = 0;
    private final String TAG = "MapsLocation";

    private static final String DATA_MIMETYPE = ContactsContract.Data.MIMETYPE;
    private static final Uri DATA_CONTENT_URI = ContactsContract.Data.CONTENT_URI;
    private static final String DATA_CONTACT_ID = ContactsContract.Data.CONTACT_ID;

    private static final String CONTACTS_ID = ContactsContract.Contacts._ID;
    private static final Uri CONTACTS_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

    private static final String STRUCTURED_POSTAL_CONTENT_ITEM_TYPE = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE;
    private static final String STRUCTURED_POSTAL_FORMATTED_ADDRESS = ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS;
    private static final String STRUCTURED_EMAIL_CONTENT_ITEM_TYPE = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // set saved context
        super.onCreate(savedInstanceState);

        // set content view
        setContentView(R.layout.main);

        // initialize UI views
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // collect user input
        final EditText address = (EditText) findViewById(R.id.address);
        final Button find = (Button) findViewById(R.id.findOnMap);

        // action handler on submit
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addr = address.getText().toString();
                addr.replace(' ', '+');

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=" + addr));

                startActivity(intent);
            }
        });

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);

                startActivityForResult(intent, PICK_FIRST_CONTACT);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Ensure that this call is the result of a successful PICK_CONTACT_REQUEST request
        if (resultCode == Activity.RESULT_OK
                && requestCode == PICK_FIRST_CONTACT) {

            // These details are covered in the lesson on ContentProviders
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(data.getData(), null, null, null, null);


            if (null != cursor && cursor.moveToFirst()) {
                String id = cursor
                        .getString(cursor.getColumnIndex(CONTACTS_ID));
                String where = DATA_CONTACT_ID + " = ? AND " + DATA_MIMETYPE
                        + " = ?";

                Cursor addrCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);

                Log.i(TAG, "got emaladdrCursor");

                if (null != addrCur && addrCur.moveToFirst()) {
                    String email = addrCur.getString(
                            addrCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    String emailType = addrCur.getString(
                            addrCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                    if (null != email) {

                        // Create Intent object for starting Google Maps application
                        Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:"
                                + email));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Location");
                        intent.putExtra(Intent.EXTRA_TEXT, "Share Location");
                        startActivity(intent);

                    }
                } else {
                    /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();*/

                    Log.i(TAG, "I did not get any email address saved");

                    Toast.makeText(MapsLocation.this, "No emails associated with this contact",
                            Toast.LENGTH_LONG).show();

                }
                if (null != addrCur)
                    addrCur.close();
            }
            if (null != cursor)
                cursor.close();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "The activity is visible and about to be started.");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "The activity is visible and about to be restarted.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "The activity is and has focus (it is now \"resumed\")");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,
                "Another activity is taking focus (this activity is about to be \"paused\")");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "The activity is no longer visible (it is now \"stopped\")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "The activity is about to be destroyed.");
    }
}
