package pl.pisze_czytam.polishnews;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        TextView repoAddress = findViewById(R.id.github_address);
        TextView emailAddress = findViewById(R.id.email_address);
        repoAddress.setOnClickListener(this);
        emailAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.github_address:
                String url = getResources().getString(R.string.repo_full_address);
                Intent openSite = new Intent(Intent.ACTION_VIEW);
                openSite.setData(Uri.parse(url));
                startActivity(openSite);
                break;
            case R.id.email_address:
                Intent intentMail = new Intent(Intent.ACTION_SENDTO);
                intentMail.setData(Uri.parse("mailto:u.drabinska@gmail.com"));
                intentMail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_title));
                if (intentMail.resolveActivity(getPackageManager()) != null) {
                    startActivity(intentMail);
                }
                break;
        }
    }
}
