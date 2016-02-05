package org.genas.headunit;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

/**
 * Created by dgey on 01.02.16.
 */
public class IoioService extends IOIOService {
    Si4703Looper si4703Looper;

    @Override
    protected IOIOLooper createIOIOLooper() {
        si4703Looper = new Si4703Looper(this);
        return si4703Looper;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case IoioActions.ACTION_TUNE:
                    si4703Looper.tune(Integer.parseInt(intent.getStringExtra(IoioActions.EXTRA_PARAM1)));
                    break;
                case IoioActions.ACTION_SEEK:
                    si4703Looper.seek(Boolean.parseBoolean(intent.getStringExtra(IoioActions.EXTRA_PARAM1)),
                            Boolean.parseBoolean(intent.getStringExtra(IoioActions.EXTRA_PARAM2)));
                    break;
                case IoioActions.ACTION_MUTE:
                    si4703Looper.setMute(Boolean.parseBoolean(intent.getStringExtra(IoioActions.EXTRA_PARAM1)));
                    break;
                case IoioActions.ACTION_SETVOLUME:
                    si4703Looper.setSi4703Volume(Integer.parseInt(intent.getStringExtra(IoioActions.EXTRA_PARAM1)));
                    break;
                case IoioActions.ACTION_POWER:
                    //si4703Looper.setPower(Boolean.parseBoolean(intent.getStringExtra(IOIOActions.EXTRA_PARAM1)));
                    break;
                case IoioActions.ACTION_SEND_COMMAND:
                    //si4703Looper.sendCommand();
                    Log.i("IoioService", "sendCommand called");
                    break;
                case IoioActions.STOPSERVICE:
                    nm.cancel(0);
                    stopSelf();
                    break;
                default:
            }
        }  else {
            // Service starting. Create a notification.
/*            Notification notification = new Notification(
                    R.drawable.icon, "IOIO service running",
                    System.currentTimeMillis());
            notification
                    .setLatestEventInfo(this, "IOIO Service", "Click to stop",
                            PendingIntent.getService(this, 0, new Intent(
                                    IoioActions.STOPSERVICE, null, this, this.getClass()), 0));
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            nm.notify(0, notification);*/
            sendBroadcast(new Intent(IoioActions.ENABLEUI));
        }
        return result;
    }

    private static void executeCommand(Context context, final String ACTION, String param1, String param2) {
        Intent intent = new Intent(context, IoioService.class);
        intent.setAction(ACTION);
        intent.putExtra(IoioActions.EXTRA_PARAM1, param1);
        intent.putExtra(IoioActions.EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    private final IIoioService.Stub.Stub mBinder = new IIoioService.Stub() {
        @Override
        public void tune(int frequency) throws RemoteException {
            executeCommand(getApplicationContext(), IoioActions.ACTION_TUNE, Integer.toString(frequency), null );
        }

        @Override
        public void mute(boolean muted) throws RemoteException {
            executeCommand(getApplicationContext(), IoioActions.ACTION_MUTE, Boolean.toString(muted), null );
        }

        @Override
        public void seek(boolean seekUp, boolean bWaitForSTC) throws RemoteException {
            executeCommand(getApplicationContext(), IoioActions.ACTION_SEEK, Boolean.toString(seekUp), Boolean.toString(bWaitForSTC) );
        }

        @Override
        public void setVolume(int volume) throws RemoteException {
            executeCommand(getApplicationContext(), IoioActions.ACTION_SETVOLUME, Integer.toString(volume), null );
        }

        @Override
        public void setPower(boolean powerOn) throws RemoteException {
            executeCommand(getApplicationContext(), IoioActions.ACTION_POWER, Boolean.toString(powerOn), null );
        }

        @Override
        public void sendCommand() throws RemoteException {
            executeCommand(getApplicationContext(), IoioActions.ACTION_SEND_COMMAND, null, null );
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }
}
