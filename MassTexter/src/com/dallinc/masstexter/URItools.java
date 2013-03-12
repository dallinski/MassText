package com.dallinc.masstexter;

import java.io.InputStream;
import android.content.*;
import android.database.Cursor;
import android.graphics.*;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.QuickContactBadge;

public class URItools {
	
	Context context;

	public void myMethod(String phoneNumber, QuickContactBadge qcb, Context c) {
		context = c;

	    String contactId = fetchContactIdFromPhoneNumber(phoneNumber); 
	    Uri uri = getPhotoUri(Long.parseLong(contactId));
	    qcb.setImageBitmap(loadContactPhoto(context.getContentResolver(), Long.parseLong(contactId)));

	}

	private String fetchContactIdFromPhoneNumber(String phoneNumber) {
	    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
	    Cursor cursor = context.getContentResolver().query(uri,
	            new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
	            null, null, null);

	    String contactId = "";

	    if (cursor.moveToFirst()) {
	        do {
	            contactId = cursor.getString(cursor.getColumnIndex(PhoneLookup._ID));
	        } while (cursor.moveToNext());
	    }

	    return contactId;
	}


	private static Bitmap loadContactPhoto(ContentResolver cr, long  id) {
	    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
	    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
	    if (input == null) {
	        return null;
	    }
	    return BitmapFactory.decodeStream(input);
	}

	private Uri getPhotoUri(long contactId) {
	    ContentResolver contentResolver = context.getContentResolver();

	    try {
	        Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
	                        null, ContactsContract.Data.CONTACT_ID + "=" + contactId
	                                + " AND " + ContactsContract.Data.MIMETYPE + "='"
	                                + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
	                                + "'", null, null);

	        if (cursor != null) {
	            if (!cursor.moveToFirst()) {
	                return null; // no photo
	            }
	        } else {
	            return null; // error in cursor process
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }

	    Uri person = ContentUris.withAppendedId(
	            ContactsContract.Contacts.CONTENT_URI, contactId);
	    return Uri.withAppendedPath(person,
	            ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}
	
	
}
