package com.logvai.logvai;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class ListaActivity2 extends ListActivity {

    // ==============================================================================================================
    // DECLARAÇÕES DIVERSAS
    public  ListView lv;
    ProgressDialog progressDialog;
    public String IdEntrega="";

    //Volley conectividade
    public String JSON_URL="";
    // ==============================================================================================================


    // ==============================================================================================================
    // CICLO DA ACTIVITY - onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista2);

        //monta ListView
        lv = (ListView) findViewById(android.R.id.list);
        progressDialog = new ProgressDialog(this);

        //recupera dados passados da Activity anterior - ID da Entrega Master
        Bundle b = getIntent().getExtras();
        IdEntrega = b.getString("IDEntrega");

        //requisita lista de entregas e preenche ListView
        JSON_URL = "http://logvaiws.azurewebsites.net/Webservice.asmx/ListaEntregas2?param1=" + IdEntrega;
        volleyStringRequst(JSON_URL);

        //aguarda/verifica seleção do usuário
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                //ID da Entrega selecionada
                String  idEntrega    = (String) lv.getItemAtPosition(position);

                //transferencia de dados entre Activitys
                Bundle b = new Bundle();
                b.putString("IDEntrega",idEntrega);

                //abre nova Activity
                Intent proximatela = new Intent(getApplicationContext(),DetalhesActivity.class);
                proximatela.putExtras(b);
                startActivity(proximatela);

            }
        });

    }
    // onResume
    @Override
    public void onResume(){
        super.onResume();
        //atualiza lista de entregas em aberto e preenche ListView
        volleyStringRequst(JSON_URL);
    }



    //======================================================================================================================
    //VOLLEY CONECTIVIDADE - TROCA DE DADOS COM WEB-SERVICE - requisita Lista de Entregas (Bairro e Endereço)
    //======================================================================================================================
    public void volleyStringRequst(String url){

        String  REQUEST_TAG = "com.logvai.logvai.Lista2";
        progressDialog.setMessage("Aguarde...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Formata retorno obtido do web-service. Layout: [{" json string "}]
                String str1 =  "{\"entregas\":" + response.toString().substring(91);
                int tamanho = str1.length() -9 ;
                String str2 = str1.substring(0,tamanho) + "}";

                //envia retorno formatado para processo de Parsing
                showJSON(str2);
                progressDialog.hide();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.hide();
            }
        });
        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, REQUEST_TAG);
    }

    //======================================================================================================================
    //JSON Parsing
    private void showJSON(String json){
        //monta Array String com lista de Entregas
        ParseJSON2 pj = new ParseJSON2(json);
        pj.parseJSON2();

        ListaAdapter2 cl = new ListaAdapter2(this, ParseJSON.IDs, ParseJSON.Titulos, ParseJSON.SubTitulos, ParseJSON.SubTitulos1);
        lv.setAdapter(cl);
    }
    //=====================================================================================================================

}