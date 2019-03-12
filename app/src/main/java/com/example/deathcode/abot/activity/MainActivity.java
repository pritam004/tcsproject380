package com.example.deathcode.abot;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.deathcode.abot.adapter.MessageAdapter;
import com.example.deathcode.abot.model.ResponseMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIEvent;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.gson.JsonElement;
import java.util.Map;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AIListener {


    EditText userInput;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<ResponseMessage> responseMessageList;
    private Button listenButton;
    private AIService aiService;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userInput = findViewById(R.id.userInput);
        recyclerView = findViewById(R.id.conversation);
        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(responseMessageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(messageAdapter);



        listenButton = (Button) findViewById(R.id.listenButton);
        //resultTextView = (TextView) findViewById(R.id.resultTextView);
        final AIConfiguration config = new AIConfiguration(
                "d6b95d48d3cf4adda389a9fd59bec9a1",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        /*AIRequest request = new AIRequest("your text request");

        try {
            aiService.textRequest(request);
        } catch (AIServiceException e) {
            Log.d("ghapla", "ghapla kheyeche");
            e.printStackTrace();
        }
        */






    }
    private void sendRequest() {


        final String contextString = String.valueOf(userInput.getText());



        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

            @Override
            protected AIResponse doInBackground(final String... params) {
                final AIRequest request = new AIRequest();
                String query = params[0];
                String event = params[1];

                if (!TextUtils.isEmpty(query))
                    request.setQuery(query);
                if (!TextUtils.isEmpty(event))
                    request.setEvent(new AIEvent(event));
                final String contextString = params[2];
                RequestExtras requestExtras = null;
                if (!TextUtils.isEmpty(contextString)) {
                    final List<AIContext> contexts = Collections.singletonList(new AIContext(contextString));
                    requestExtras = new RequestExtras(contexts, null);
                }

               /* try {
                    return aiService.textRequest(request, requestExtras);
                } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                    return null;
                }*/
                AIRequest request1 = new AIRequest(contextString);

                try {
                   return aiService.textRequest(request1);
                } catch (AIServiceException e) {
                    Log.d("ghapla", "ghapla kheyeche");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final AIResponse response) {
                if (response != null) {
                    onResult(response);
                } else {
                    onError(aiError);
                }
            }
        };

        task.execute("hello", "hello", contextString);
    }


    boolean isLastVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();
        int numItems = recyclerView.getAdapter().getItemCount();
        return (pos >= numItems);
    }

    public void listenButtonOnClick(final View view) {
        aiService.startListening();
      //  sendRequest();
    }
    public void sendButtonOnClick(final View view) {
        // aiService.startListening();
        sendRequest();
        userInput.setText("");
    }



    public void onResult(final AIResponse response) {
        Result result = response.getResult();

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        // Show results in TextView.
       /* resultTextView.setText("Query:" + result.getResolvedQuery() +
                "\nAction: " + result.getFulfillment().getSpeech() +
                "\nParameters: " + parameterString);*/
       //userInput.setText(result.getResolvedQuery()+"yes");
        ResponseMessage responseMessage2 = new ResponseMessage(result.getResolvedQuery(), true);
        responseMessageList.add(responseMessage2);
        messageAdapter.notifyDataSetChanged();
        ResponseMessage responseMessage1 = new ResponseMessage(result.getFulfillment().getSpeech(), false);
        responseMessageList.add(responseMessage1);
        messageAdapter.notifyDataSetChanged();
    }
    @Override
    public void onError(final AIError error) {
        //userInput.setText(error.toString());
    }
    @Override
    public void onListeningStarted() {}

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {}

    @Override
    public void onAudioLevel(final float level) {}
}
