package com.dallinc.masstexter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.QuickContactBadge;
import java.io.InputStream;

public class URItools {
    Context context;

    private String fetchContactIdFromPhoneNumber(String string) {
        Uri uri = Uri.withAppendedPath((Uri)(ContactsContract.PhoneLookup.CONTENT_FILTER_URI), (String)(Uri.encode((String)(string))));
        Cursor cursor = this.context.getContentResolver().query(uri, new String[]{"display_name", "_id"}, (String)(null), (String[])(null), (String)(null));
        String string2 = "";
        if (!(cursor.moveToFirst())) return string2;
        do {
            string2 = cursor.getString(cursor.getColumnIndex("_id"));
        } while (cursor.moveToNext());
        return string2;
    }

    private Uri getPhotoUri(long l) {
        Cursor cursor;
        ContentResolver contentResolver = this.context.getContentResolver();
        try {
            cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, (String[])(null), ("contact_id=" + l + " AND " + "mimetype" + "='" + "vnd.android.cursor.item/photo" + "'"), (String[])(null), (String)(null));
            if (cursor == null) return null;
        }
        catch (Exception var4_5) {
            var4_5.printStackTrace();
            return null;
        }
        boolean bl = cursor.moveToFirst();
        if (bl) return Uri.withAppendedPath((Uri)(ContentUris.withAppendedId((Uri)(ContactsContract.Contacts.CONTENT_URI), (long)(l))), (String)("photo"));
        return null;
    }

    private static Bitmap loadContactPhoto(ContentResolver contentResolver, long l) {
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream((ContentResolver)(contentResolver), (Uri)(ContentUris.withAppendedId((Uri)(ContactsContract.Contacts.CONTENT_URI), (long)(l))));
        if (inputStream != null) return BitmapFactory.decodeStream((InputStream)(inputStream));
        return null;
    }

    public void setContactUri(String string, QuickContactBadge quickContactBadge, Context context) {
        this.context = context;
        String string2 = this.fetchContactIdFromPhoneNumber(string);
        try {
            Long long_ = Long.parseLong((String)(string2));
            this.getPhotoUri(long_);
            quickContactBadge.setImageBitmap(URItools.loadContactPhoto(this.context.getContentResolver(), long_));
            return;
        }
        catch (NumberFormatException var5_6) {
            var5_6.printStackTrace();
            return;
        }
    }
}

