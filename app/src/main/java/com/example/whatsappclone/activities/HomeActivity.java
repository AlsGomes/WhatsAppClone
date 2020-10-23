package com.example.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.whatsappclone.R;
import com.example.whatsappclone.database.FirebaseManager;
import com.example.whatsappclone.fragments.ContatosFragment;
import com.example.whatsappclone.fragments.ConversasFragment;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class HomeActivity extends AppCompatActivity {

    private MaterialSearchView searchView_Home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControllers();
    }

    private void initControllers() {
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsAppClone");
        setSupportActionBar(toolbar);

        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Conversas", ConversasFragment.class)
                .add("Contatos", ContatosFragment.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout smartTabLayout = (SmartTabLayout) findViewById(R.id.smartTabLayout);
        smartTabLayout.setViewPager(viewPager);

        searchView_Home = findViewById(R.id.searchView_Home);
        searchView_Home.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()) {
                    ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);
                    fragment.filterConversas(newText);
                }

                return true;
            }
        });

        searchView_Home.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
                ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);
                fragment.onStart();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem item = menu.findItem(R.id.btnPesquisar);
        searchView_Home.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnSair:
                FirebaseManager.getFirebaseAuth().signOut();
                finish();
                break;

            case R.id.btnConfig:
                startActivity(new Intent(getApplicationContext(), ConfigurationActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
