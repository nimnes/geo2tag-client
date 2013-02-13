package com.petrsu.geo2tag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 30.01.13
 * Time: 20:55
 * To change this template use File | Settings | File Templates.
 */
public class ChannelsActivity extends Activity {
    protected static JSONArray channels = null;
    protected ListView channelsView;
    ChannelAdapter channelAdapter = null;
    ArrayList<Channel> channelsList = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channels_list);

        Bundle extras = getIntent().getExtras();
        String apiResponse = extras.getString("API_RESPONSE");

        channelsList = new ArrayList<Channel>();
        try {
            channels = new JSONObject(apiResponse).getJSONArray("channels");

            for (int i = 0; i < channels.length(); i++) {
                JSONObject c = channels.getJSONObject(i);

                String name = c.getString("name");
                String description = c.getString("description");
                boolean subscribed = Geo2Tag.getInstance().subscribedChannels.contains(name);
                Channel ch = new Channel(name, description, subscribed);

                channelsList.add(ch);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        channelAdapter = new ChannelAdapter(this, R.layout.channel_info, channelsList);

        channelsView = (ListView) findViewById(R.id.channels_list);
        channelsView.setAdapter(channelAdapter);
    }

    private class ChannelAdapter extends ArrayAdapter<Channel> {
        private ArrayList<Channel> channelsList;

        public ChannelAdapter(Context context, int textViewResourseId,
                              ArrayList<Channel> channelsList) {
            super(context, textViewResourseId, channelsList);
            this.channelsList = new ArrayList<Channel>();
            this.channelsList.addAll(channelsList);
        }

        private class ViewHolder {
            TextView name;
            CheckBox checkBox;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.channel_info, null);

                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.channel_name);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Channel channel = (Channel) cb.getTag();
                        channel.setSubscribed(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Channel channel = channelsList.get(position);
            holder.name.setText(channel.getName());
            holder.checkBox.setChecked(channel.isSubscribed());
            holder.checkBox.setTag(channel);

            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();

                ArrayList<String> subscribedChannels = new ArrayList<String>();
                for (int i = 0; i < channelsList.size(); i++) {
                    if (channelsList.get(i).isSubscribed()) {
                        subscribedChannels.add(channelsList.get(i).name);
                    }
                }

                // return list of subscribed channels back to main activity
                intent.putExtra("SUBSCRIBED_CHANNELS", subscribedChannels);
                setResult(RESULT_OK, intent);

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
