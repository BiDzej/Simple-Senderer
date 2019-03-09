package com.test.menutest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Main activity class. It responses for user requests from menu panel such as
 * sending emails, sharing app, main functions of the application etc.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Start DeviceSearcher
        DeviceSearcher deviceSearcher = new DeviceSearcher();
        deviceSearcher.start();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SendPicturesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_transfer);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch(item.getItemId()) {
            case R.id.nav_camera:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewPhotoFragment()).commit();
                break;
            case R.id.nav_transfer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SendPicturesFragment()).commit();
                break;
            case R.id.nav_add_device:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddDeviceFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_share:
                Intent intentShare = new Intent (Intent.ACTION_SEND);
                intentShare.setType("text/html");
                intentShare.putExtra(Intent.EXTRA_SUBJECT, "Wow, this app is great!");
                intentShare.putExtra(Intent.EXTRA_TEXT, "Check it now!\nhttps://jurcus111.wixsite.com/simplesenderer");
                startActivity(Intent.createChooser(intentShare, "Share app"));
                break;
            case R.id.nav_rate:

                break;
            case R.id.nav_info:
                String url = "https://jurcus111.wixsite.com/simplesenderer";
                Intent intentInfo = new Intent(Intent.ACTION_VIEW);
                intentInfo.setData(Uri.parse(url));
                startActivity(Intent.createChooser(intentInfo, "Visit our website"));
                break;
            case R.id.nav_contact:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"simplesenderer@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "[android] Support");
                startActivity(Intent.createChooser(intent, "Send Email"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
