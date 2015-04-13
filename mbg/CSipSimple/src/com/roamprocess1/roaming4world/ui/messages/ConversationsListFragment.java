/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.roamprocess1.roaming4world.ui.messages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri.Builder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipMessage;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.roaming4world.CurrentFragment;
import com.roamprocess1.roaming4world.roaming4world.SetSenderUriInfo;
import com.roamprocess1.roaming4world.roaming4world.VersionHelper;
import com.roamprocess1.roaming4world.service.SipNotifications;
import com.roamprocess1.roaming4world.ui.MessageSipUri;
import com.roamprocess1.roaming4world.ui.SipHome.ViewPagerVisibilityListener;
import com.roamprocess1.roaming4world.ui.messages.ConversationsAdapter.ConversationListItemViews;
import com.roamprocess1.roaming4world.widgets.CSSListFragment;

/**
 * This activity provides a list view of existing conversations.
 */
public class ConversationsListFragment extends CSSListFragment implements ViewPagerVisibilityListener {
	//private static final String THIS_FILE = "Conv List";
	
    // IDs of the main menu items.
    public static final int MENU_COMPOSE_NEW          = 0;
    public static final int MENU_DELETE_ALL           = 1;

    // IDs of the context menu items for the list of conversations.
    public static final int MENU_DELETE               = 0;
    public static final int MENU_VIEW                 = 1;
	
    private boolean mDualPane;
    private String stored_account_register_status,preRegValue;
    private ConversationsAdapter mAdapter;
    private View mHeaderView;
    ImageButton imgButton_Chat, imgRightMenu;
    private SharedPreferences prefs;
    String stored_chatuserNumber, stored_user_country_code;

    public DBContacts sqliteadapter;
    Cursor returned_refresh_val;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        deleteRegistrarAck();
        
        setHasOptionsMenu(true);
        onVisibilityChanged_custom(true);
        ListView lv = getListView();
        
        if(getListAdapter() == null && mHeaderView != null) {
            lv.addHeaderView(mHeaderView, null, true);
        }
        
        lv.setOnCreateContextMenuListener(this);
    }
    
    private void attachAdapter() {
        // Header view add
        if(mAdapter == null) {
            // Adapter
            mAdapter = new ConversationsAdapter(getActivity(), null);
        }
        setListAdapter(mAdapter);
          
    }
    
    public void inValidateActionBar() {
    	 if (Build.VERSION.SDK_INT >= 11)
         {
             VersionHelper.refreshActionBarMenu(getActivity());
         }
	}
    

    public static ConversationsListFragment newInstance(String text) {

    	ConversationsListFragment f = new ConversationsListFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
    
    @SuppressLint("NewApi") @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View v = inflater.inflate(R.layout.message_list_fragment, container, false);
        LinearLayout progressContainer = (LinearLayout) v.findViewById(R.id.progressContainer);
   
        prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);		
        stored_chatuserNumber = "com.roamprocess1.roaming4world.stored_chatuserNumber";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		
		
		
		stored_account_register_status = "com.roamprocess1.roaming4world.account_register_status";
		preRegValue=prefs.getString(stored_account_register_status, "Connecting"); 
		System.out.println("oncreate prefValue" + preRegValue);
		
		LinearLayout onlinelayout = (LinearLayout) v.findViewById(R.id.onlineStatus);
		LinearLayout header_onlinelayout = (LinearLayout) v.findViewById(R.id.ll_dialer_onlineStatus);
		
		TextView status_register_account = (TextView) v.findViewById(R.id.status);
	
		ConnectivityManager connMgr1 = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo1 = connMgr1.getActiveNetworkInfo();
		if (networkInfo1 != null && networkInfo1.isConnected()) {

			if (status_register_account != null) {
				if (preRegValue.equals("Online")) {
					status_register_account.invalidate();
					onlinelayout.setBackgroundColor(Color.parseColor("#8CC63F"));
					status_register_account.setText("Online");
					status_register_account.invalidate();
					onlinelayout.setVisibility(LinearLayout.GONE);
					header_onlinelayout.setBackgroundColor(Color.parseColor("#8CC63F"));
				} else if (preRegValue.equals("Connecting")) {
					onlinelayout.setBackgroundColor(Color.parseColor("#FFA500"));
					status_register_account.setText("Connecting");
					status_register_account.invalidate();
					onlinelayout.invalidate();
					header_onlinelayout.setBackgroundColor(Color.parseColor("#FFA500"));
					onlinelayout.setVisibility(LinearLayout.GONE);
			}
			}
		} else {
			System.out.println("updateRegistrationsState() : No network");
			status_register_account.invalidate();
			onlinelayout.setBackgroundColor(Color.parseColor("#EB3D35"));
			status_register_account.setText("No Internet Connection");
			status_register_account.invalidate();
			onlinelayout.invalidate();
			onlinelayout.setVisibility(LinearLayout.GONE);
			header_onlinelayout.setBackgroundColor(Color.parseColor("#EB3D35"));
			
		}
		

        if(CurrentFragment.progressContainerLayout == false)
        {
        	progressContainer.setVisibility(LinearLayout.GONE);
        }
        CurrentFragment.progressContainerLayout = true;
        
        
        ListView lv = (ListView) v.findViewById(android.R.id.list);

        View.OnClickListener addClickButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	System.out.println("conversationsListFragment.java in oncreateview in onClick listener");
                onClickAddMessage();
            }
        };
        

        // Header view
        mHeaderView = (ViewGroup)inflater.inflate(R.layout.conversation_list_item, lv, false);
       ((TextView) mHeaderView.findViewById(R.id.from) ).setText(R.string.new_message);
        ((TextView) mHeaderView.findViewById(R.id.subject) ).setText(R.string.create_new_message);
        mHeaderView.findViewById(R.id.quick_contact_photo).setVisibility(View.GONE);
        mHeaderView.setOnClickListener(addClickButtonListener);
        // Empty view
        Button bt = (Button) v.findViewById(android.R.id.empty);
        
      //  bt.setOnClickListener(addClickButtonListener);
        bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent= new Intent(getActivity(),MessageSipUri.class);
				getActivity().startActivity(intent);
			}
		});
        /*
        View vv = getActivity().getActionBar().getCustomView();
        imgButton_Chat = (ImageButton) vv.findViewById(R.id.imgRightMenu_chat);
        imgRightMenu = (ImageButton) vv.findViewById(R.id.imgRightMenu);
        
        
		
        imgButton_Chat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickAddMessage();
			}
		});*/
        
        return v;
    }
    
    private void onClickAddMessageForword(Context context) {
    	sqliteadapter=new DBContacts(context);
    	sqliteadapter.openToWrite();
    	sqliteadapter.update_refresh_table("yes");
    	System.out.println("in update yes");
    	sqliteadapter.close();
    	//System.out.println("ConversationsListFragment.java in onclickAddMessage() getListView is "+-getListView().getHeaderViewsCount());
        viewDetails(-getListView().getHeaderViewsCount(), null);
    }
    
    
    public void onClickAddMessage() {
    	sqliteadapter=new DBContacts(getActivity());
    	sqliteadapter.openToWrite();
    	sqliteadapter.update_refresh_table("no");
    	System.out.println("in update no");
    	sqliteadapter.close();
    	
        
    	System.out.println("ConversationsListFragment.java in onclickAddMessage() getListView is "+-getListView().getHeaderViewsCount());
        viewDetails(-getListView().getHeaderViewsCount(), null);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View management
        mDualPane = getResources().getBoolean(R.bool.use_dual_panes);

        // Modify list view
        ListView lv = getListView();
        lv.setVerticalFadingEdgeEnabled(true);
        // lv.setCacheColorHint(android.R.color.transparent);
        if (mDualPane) {
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lv.setItemsCanFocus(false);
        } else {
            lv.setChoiceMode(ListView.CHOICE_MODE_NONE);
            lv.setItemsCanFocus(true);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        SipNotifications nManager = new SipNotifications(getActivity());
        nManager.cancelMessages();
        deleteRegistrarAck();
    }

    public void viewDetails(int position, ConversationListItemViews cri) {
        String number = null;
        String fromFull = null;
        if(cri != null) {
            number = cri.getRemoteNumber();
            number = CurrentFragment.stripContactNumber(number, prefs.getString(stored_user_country_code, "No Value"));
            Drawable d = cri.quickContactView.getImageView().getDrawable();
            SetSenderUriInfo.senderImageBitmap = ((BitmapDrawable)d).getBitmap();
            prefs.edit().putString(stored_chatuserNumber, number+"").commit();
            System.out.println("sharedpref number is "+prefs.getString(stored_chatuserNumber, "No Value"));
            fromFull = cri.fromFull;
        }
    	viewDetails(position, number, fromFull);
    	
    	Log.d("fromviewdetails", "0");
    	Log.d("position", position+" 0");
    	Log.d("number", number+" 0");
    	Log.d("fromFull", fromFull+" 0");
    }
    
    
    public void viewDetails(int position, String number, String fromFull) {
        System.out.println("ConversationListFragments.java in viewDetails() number is "+number + "fromFull is "+fromFull+ "position is "+position);
        Bundle b = MessageFragment.getArguments(number, fromFull);
		if (number != null) {
			if (mDualPane) {
				// If we are not currently showing a fragment for the new
				// position, we need to create and install a new one.
				MessageFragment df = new MessageFragment();
				df.setArguments(b);
				// Execute a transaction, replacing any existing fragment
				// with this one inside the frame.
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.replace(R.id.details, df, null);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();

				getListView().setItemChecked(position, true);
			} else {
				Intent it = new Intent(getActivity(), MessageActivity.class);
				it.putExtras(b);
				startActivity(it);
			}
		}
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), SipMessage.THREAD_URI, null, null, null, null);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (info.position >= 0) {
            menu.add(0, MENU_VIEW, 0, R.string.menu_view);
            menu.add(0, MENU_DELETE, 0, R.string.menu_delete);
        }
    }
    
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
      
        
        System.out.println("Converseattion onContextItemSelected");
        
        if (info.position >= 0) {
            ConversationListItemViews cri = (ConversationListItemViews) info.targetView.getTag();
            System.out.println("Converseattion:"+cri);
            
            if(cri != null) {
                switch (item.getItemId()) {
                case MENU_DELETE: {
                    confirmDeleteThread(cri.getRemoteNumber());
                    break;
                }
                case MENU_VIEW: {
                    viewDetails(info.position, cri);
                    break;
                }
                default:
                    break;
                }
            }
        }
        return super.onContextItemSelected(item);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ConversationListItemViews cri = (ConversationListItemViews) v.getTag();
        System.out.println("ConversationListFragment:onListItemClick");
        System.out.println("ConversationListFragment:cr:"+cri);
        viewDetails(position, cri);
    }

    
    @Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}
    
    

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

	private void confirmDeleteThread(final String from) {
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_dialog_title)
            .setIcon(android.R.drawable.ic_dialog_alert)
        .setCancelable(true)
        .setPositiveButton(R.string.delete, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d("from onClick", from + " @");
				if(TextUtils.isEmpty(from)) {
				    getActivity().getContentResolver().delete(SipMessage.MESSAGE_URI, null, null);
				}else {
				    Builder threadUriBuilder = SipMessage.THREAD_ID_URI_BASE.buildUpon();
				    threadUriBuilder.appendEncodedPath(from);
				    getActivity().getContentResolver().delete(threadUriBuilder.build(), null, null);
				}
			}
		})
        .setNegativeButton(R.string.no, null)
        .setMessage(TextUtils.isEmpty(from)
                ? R.string.confirm_delete_all_conversations
                        : R.string.confirm_delete_conversation)
        .show();
    }

	private void deleteRegistrarAck() {
		Log.d("ConversationsListFragment deleteRegistrarAck()", "called");
		try {
			String registrar = "sip:registrar@kamailio.org" ;
			if(TextUtils.isEmpty(registrar)) {
			    getActivity().getContentResolver().delete(SipMessage.MESSAGE_URI, null, null);
			}else {
			    Builder threadUriBuilder = SipMessage.THREAD_ID_URI_BASE.buildUpon();
			    threadUriBuilder.appendEncodedPath(registrar);
			    getActivity().getContentResolver().delete(threadUriBuilder.build(), null, null);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
    boolean alreadyLoaded = false;

    @Override
    public void onVisibilityChanged(boolean visible) {
    	
    	deleteRegistrarAck();
    	
        if(visible) {
            attachAdapter();
            // Start loading
            if(!alreadyLoaded) {
                getLoaderManager().initLoader(0, null, this);
                alreadyLoaded = true;
            }
        }
        
        if (visible && isResumed()) {
            ListView lv = getListView();
            if (lv != null && mAdapter != null) {
                final int checkedPos = lv.getCheckedItemPosition();
                if (checkedPos >= 0) {
                    // TODO post instead
                    Thread t = new Thread() {
                        public void run() {
                            Cursor c = (Cursor) mAdapter.getItem(checkedPos - getListView().getHeaderViewsCount());
                            if(c != null) {
                                String from = c.getString(c.getColumnIndex(SipMessage.FIELD_FROM));
                                String to = c.getString(c.getColumnIndex(SipMessage.FIELD_TO));
                                final String fromFull = c.getString(c.getColumnIndex(SipMessage.FIELD_FROM_FULL));
                                String number = from;
                                if (SipMessage.SELF.equals(number)) {
                                    number = to;
                                }
                                final String nbr = number;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewDetails(checkedPos, nbr, fromFull);
                                    }
                                });
                            }
                        };
                    };
                    t.start();
                }
            }
        }
    }

    public void onVisibilityChanged_custom(boolean visible) {

    	deleteRegistrarAck();
    	
        if(visible) {
            attachAdapter();
            // Start loading
            if(!alreadyLoaded) {
                getLoaderManager().initLoader(0, null, this);
                alreadyLoaded = true;
            }
        }
        
        if (visible && isResumed()) {
            ListView lv = getListView();
            if (lv != null && mAdapter != null) {
                final int checkedPos = lv.getCheckedItemPosition();
                if (checkedPos >= 0) {
                    // TODO post instead
                    Thread t = new Thread() {
                        public void run() {
                            Cursor c = (Cursor) mAdapter.getItem(checkedPos - getListView().getHeaderViewsCount());
                            if(c != null) {
                                String from = c.getString(c.getColumnIndex(SipMessage.FIELD_FROM));
                                String to = c.getString(c.getColumnIndex(SipMessage.FIELD_TO));
                                final String fromFull = c.getString(c.getColumnIndex(SipMessage.FIELD_FROM_FULL));
                                String number = from;
                                if (SipMessage.SELF.equals(number)) {
                                    number = to;
                                }
                                final String nbr = number;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewDetails(checkedPos, nbr, fromFull);
                                    }
                                });
                            }
                        };
                    };
                    t.start();
                }
            }
        }
    }
    
    
    @Override
    public void changeCursor(Cursor c) {
    	
        if(mAdapter != null) {
            mAdapter.changeCursor(c);
        }
    }
	
   public Intent getIntentval()
   {
	  return getActivity().getIntent(); 
   }
   
   public void forwordMessage(Context context)
   {
	  System.out.println("forwordMessage");
	  onClickAddMessageForword(context);
   }
}
