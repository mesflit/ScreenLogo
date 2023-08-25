package github.mesflit.screenlogo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 123; // Özel bir kod numarası

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences'tan kayıtlı metni alın
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String kayitliMetin = sharedPreferences.getString("kayitliMetin", ""); // İlk parametre anahtar, ikinci parametre varsayılan değer

        // EditText bileşenini bulun
        EditText editText = findViewById(R.id.edittext);

        // EditText'e kayıtlı metni ayarlayın
        editText.setText(kayitliMetin);

        Button saveButton = findViewById(R.id.savebutton);
        Button startButton = findViewById(R.id.startButton);
        Button githubButton = findViewById(R.id.githubButton);
        
        githubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Açmak istediğiniz URL'yi belirtin
                String url = "https://github.com/mesflit/ScreenLogo";

                // Tarayıcıyı açmak için bir Intent oluşturun
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                // Intent'i başlatın
                startActivity(intent);
            }
        });
        
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Diğer uygulamaların üzerine çizme iznini kontrol edin
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    // İzin iste
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
                } else {
                    // İzin zaten verilmiş, işlemlerinize devam edebilirsiniz.
                    // Kendi paket adını aç
                    Intent intent = new Intent(MainActivity.this, OverlayService.class);
                    startService(intent);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String metin = editText.getText().toString(); // EditText'ten metni alın.
                // Metni istediğiniz şekilde kaydedebilirsiniz, örneğin SharedPreferences veya veritabanı kullanarak.
                // SharedPreferences örneği:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("kayitliMetin", metin);
                editor.apply();
            }
        });
    }
}
