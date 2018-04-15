package org.synergy.net

import android.os.AsyncTask
import org.synergy.client.Client

class SynergyConnectTask : AsyncTask<Client, Unit, Unit>() {
    override fun doInBackground(vararg params: Client?) {
        for (client in params) client?.connect()
    }
}