package cgi.una.ac.cr.archivodemo;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public final int EXTERNAL_REQUEST = 138;


    Switch visual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        visual = (Switch)findViewById(R.id.switch1);
        visual.setChecked(this.getSharedPreferences(
                getString(R.string.preference_visual), Context.MODE_PRIVATE).getBoolean(getString(R.string.preference_visual), false));
        if ( isExternalStorageReadable() ) {
            if (requestForPermission()) {
                String sdCardState = Environment.getExternalStorageState();
                if (!sdCardState.equals(Environment.MEDIA_MOUNTED)) {
                    //displayMessage("No SD Card.");
                    return;
                } else {
                    File root = Environment.getExternalStorageDirectory();
                    lookForFilesAndDirectories(root);
                }
            }
        }

    }

    public void lookForFilesAndDirectories(File file) {
        if( file.isDirectory() ) {
            String[] filesAndDirectories = file.list();
            for( String fileOrDirectory : filesAndDirectories) {
                File f = new File(file.getAbsolutePath() + "/" + fileOrDirectory);
                Log.d("Directorio:" , file.getName());
                lookForFilesAndDirectories(f);
            }
        } else {
            Log.d("Archivo:" , file.getName());

        }
    }


    public void guardarBtn(View view) {


        this.getSharedPreferences(
                getString(R.string.preference_visual), Context.MODE_PRIVATE).edit().putBoolean(getString(R.string.preference_visual), visual.isChecked() ).commit();

    }
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }





    public boolean requestForPermission() {

        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return isPermissionOn;
    }

    public boolean canAccessExternalSd() {
        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));

    }
}
