package k.studio.tiktokrec.ui.permissions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import k.studio.tiktokrec.R

class PermissionGrantInstructionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_grant_instruction)

        findViewById<ConstraintLayout>(R.id.root).setOnClickListener {
            finishAffinity()
        }
    }
}