package br.edu.fa7.projeto10;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ExtratoActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {
	
	private ListView extratoListView;
	private TextView saldo;
	
	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_extrato);
		
		this.lancamentos.add(new Lancamento(11.3, true, "Venda"));
		this.lancamentos.add(new Lancamento(20.3, false, "Comida"));
		
		this.extratoListView = (ListView) findViewById(R.id.extratoview);
		this.extratoListView.setAdapter(new ExtratoAdpter(this, lancamentos));
		this.extratoListView.setOnItemClickListener(this);
		this.extratoListView.setOnItemLongClickListener(this);
		
		this.saldo = (TextView) findViewById(R.id.saldo);
		calcularSaldo();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.extrato, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0 && resultCode == RESULT_OK) {
			if(data.getStringExtra("op").equals("add")) {
				//data.getSerializableExtra("xpto")
				Lancamento l = new Lancamento(Double.valueOf(data.getStringExtra("valor")), data.getStringExtra("tipo").equals("1"), data.getStringExtra("descricao"));
				this.lancamentos.add(l);
			} else {
				Lancamento l = this.lancamentos.get(data.getIntExtra("index",0));
					l.setValue(Double.valueOf(data.getStringExtra("valor")));
					l.setIsReceita(data.getStringExtra("tipo").equals("1"));
					l.setTipoLancamento(data.getStringExtra("descricao"));
			}
			this.extratoListView.setAdapter(new ExtratoAdpter(this, lancamentos));
			calcularSaldo();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
		Lancamento l = this.lancamentos.get(pos);
		Intent i = new Intent(this, ManterLancamentoActivity.class);
			i.putExtra("op", "edit");
			i.putExtra("index", pos);
			i.putExtra("valor", l.getValue().toString());
			i.putExtra("tipo", l.getIsReceita() ? "1" : "2" );
			i.putExtra("descricao", l.getTipoLancamento());
			
		startActivityForResult(i, 0);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			Intent i = new Intent(this, ManterLancamentoActivity.class);
			i.putExtra("op", "add");
		startActivityForResult(i, 0);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void calcularSaldo() {
		Double saldo = new Double(0.0);
		for(Lancamento l : this.lancamentos) {
			if(l.getIsReceita()) {
				saldo += l.getValue();
			} else {
				saldo -= l.getValue();						
			}
		}
		
		this.saldo.setText("Saldo: R$ " + saldo.toString());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		this.lancamentos.remove(arg2);
		this.extratoListView.setAdapter(new ExtratoAdpter(this, lancamentos));
		calcularSaldo();
		return false;
	}
	
}
