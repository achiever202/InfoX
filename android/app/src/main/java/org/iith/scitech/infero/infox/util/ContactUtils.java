package org.iith.scitech.infero.infox.util;

import java.util.Vector;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONObject;

/*
 * This class provides the contact details.
 */
public class ContactUtils
{
	/* content resolver for the contacts contact provider. */
	private ContentResolver resolver;
	
	/* cursor object to query the contacts table. */
	private Cursor cursor;

    private static char[] alphabets = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'};
	/* constructor for the objects. */
	public ContactUtils(ContentResolver contentResolver) 
	{
		resolver = contentResolver;
		cursor = null;
	}
	
	/* This function returns the entire contact list from the device. */
	public Cursor getAllContacts()
	{
		/* if the cursor is initialized before, closing the cursor. */
		if(cursor!=null && !cursor.isClosed())
			cursor.close();
		
		/* getting the contact list. */
		cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		return cursor;
	}
	
	/* This function returns the contact numbers as an array of strings. */
	public String[] getAllContactNumbers()
	{
		/* getting the entire contact list. */
		getAllContacts();
		
		/* iterating over the list and getting the contact numbers. */
		String[] contacts = new String[cursor.getCount()];
		int index = 0;
		while(cursor.moveToNext())
		{
			contacts[index] = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))+"\n";
			index++;
		}
		
		if(cursor!=null && !cursor.isClosed())
			cursor.close();
		return contacts;
	}

    public String[] getAllContactNumbersAndName()
    {
		/* getting the entire contact list. */
        getAllContacts();

		/* iterating over the list and getting the contact numbers. */
        String[] contacts = new String[cursor.getCount()];
        int index = 0;
        JSONObject jsonObject = new JSONObject();
        while(cursor.moveToNext())
        {
            try {
                jsonObject.put("name", cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                jsonObject.put("number", cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            contacts[index] = jsonObject.toString();
            index++;
        }

        if(cursor!=null && !cursor.isClosed())
            cursor.close();
        return contacts;
    }


	
	public String getContactName(String contactNumber)
	{
		String contactName = "";
		
		/* getting the entire contact list. */
		getAllContacts();
		
		while(cursor.moveToNext())
		{
			if(contactNumber.equals(getFormattedContact(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))))
			{
				contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				break;
			}
		}
		
		if(cursor!=null && !cursor.isClosed())
			cursor.close();
		
		return contactName;
	}
	
	/* This function builds a formatted 10 digit contact number from the contact string provided. */
	public static String getFormattedContact(String contact)
	{
		/* if the number is less than 10 digits, not valid. */
		if(contact.length()<10)
			return "";
		
		String formattedContact = "";
		for(int i=contact.length()-1, j=0; i>=0 && j<10; i--)
		{
			if(contact.charAt(i)>='0' && contact.charAt(i)<='9')
			{
				formattedContact += contact.charAt(i);
				j++;
			}
		}
		
		if(formattedContact.length()<10)
			return "";
		
		/* The number should begin with a number other than 0. */
		if(formattedContact.charAt(9)=='0')
			return "";
		
		return new StringBuilder(formattedContact).reverse().toString();
	}

	public static String getAlphabetContact(String contact)
	{
		String alphabetContact = "";
		for(int i=0; i<contact.length(); i++)
			alphabetContact += String.valueOf(alphabets[Integer.parseInt(String.valueOf(contact.charAt(i)))]);

        //Log.v("DEB", contact+" , "+alphabetContact);
		return alphabetContact;
	}
}
