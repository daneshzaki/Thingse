package com.daneshzaki.thingse;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.text.method.MovementMethod;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by danesh on 9/11/2015.
 */
public class OpenSrcLicenses extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.getWindow().setNavigationBarColor(Color.parseColor("#D0D0D0"));
        setContentView(R.layout.activity_opensrc_licenses);

        //open src licenses
        String licenses[] = {"makovkastar's FloatingActionButton \nhttps://github.com/makovkastar/FloatingActionButton\n Copyright (c) 2014 Oleksandr Melnykov\n",
        "\netsy's AndroidStaggeredGrid \nhttps://github.com/etsy/AndroidStaggeredGrid\n Copyright (c) 2013 Etsy \n",
        "\njdamcd's android-crop \nhttps://github.com/jdamcd/android-crop\n Copyright (c) 2015 SoundCloud\n",
        "\nIcons by http://www.icons4android.com/ \n",
        "\nThe MIT License (MIT)\n",
        "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), " ,
                "to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense," ,
                " and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:" ,
                " The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. " ,
                "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, " ,
                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, " ,
                        "WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE." ,
                "\n\nLicensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at " ,
                "http://www.apache.org/licenses/LICENSE-2.0",
                "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" " ,
                        "BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. " ,
            "See the License for the specific language governing permissions and limitations under the License."};

        //set fonts for all text
        Typeface typeface = Typeface.createFromAsset( getResources().getAssets(), "SourceSansPro-Regular.otf");

        TextView licenseView = ((TextView) findViewById(R.id.licenseView));

        licenseView.setTypeface(typeface);
        //join all strings
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < licenses.length; i++)
        {
            builder.append(licenses[i]);
        }

        licenseView.setText(builder.toString());
        licenseView.setMovementMethod(new ScrollingMovementMethod().getInstance());



        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
        actionBar.setTitle("Open Source Licenses");
        actionBar.setHomeButtonEnabled(true);

    }


}

