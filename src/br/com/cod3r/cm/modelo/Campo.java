package br.com.cod3r.cm.modelo;

import java.util.ArrayList;
import java.util.List;

public class Campo {
	
	private boolean minado;
	private boolean aberto;
	private boolean marcado;
	
	private List<Campo> vizinhos = new ArrayList<>();
	private List<CampoObservador> observadores = new ArrayList<>();
	//usar Set ao inves de List para evitar que o mesmo observer seja registrado duas vezes sem a necessidade de controlar
	//privatte List<BiConsumer<Campo, CampoEvento> observadores = new ArrayList<>();// nao precisa criar a interfaces
	
	
	private final int coluna;
	private final int linha;
	
	public Campo(int coluna, int linha) {
		this.coluna = coluna;
		this.linha = linha;
	}
	
	//metodo para registrar os observadores
	public void registrarObservador(CampoObservador observador) {
		observadores.add(observador);
	}
	
	//metodo executado sempre que um evento ocorrer
	private void notificarObservadores(CampoEvento evento) {
		observadores.stream().forEach(o -> o.eventoOcorreu(this, evento));
	}
	
	public boolean adicionarVizinho(Campo vizinho) {
		boolean linhaDiferente = linha != vizinho.linha;
		boolean colunaDiferente = coluna != vizinho.coluna;
		boolean diagonal = linhaDiferente && colunaDiferente;
		
		int deltaLinha = Math.abs(linha - vizinho.linha);
		int deltaColuna =  Math.abs(coluna - vizinho.coluna);
		int deltaGeral = deltaColuna + deltaLinha;
		
		if(deltaGeral == 1 && !diagonal) {
			vizinhos.add(vizinho);
			return true;
		}else if(deltaGeral == 2 && diagonal){
			vizinhos.add(vizinho);
			return true;
		} else {
			return false;
		}
	}
	
	public void alternarMarcacao() {
		if(!aberto) {
			marcado = !marcado;
			if(marcado) {
				notificarObservadores(CampoEvento.MARCAR);
			} else {
				notificarObservadores(CampoEvento.DESMARCAR);
			}
		}
	}
	
	public boolean abrir() {
		if(!aberto && !marcado) {
			if(minado) {
				notificarObservadores(CampoEvento.EXPLODIR);
				return true;
			}
			
			setAberto(true);
			if(vizinhancaSegura()) {
				vizinhos.forEach(v -> v.abrir());
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void minar() {
		minado = true;
	}
	
	public boolean isMinado() {
		return minado;
	}
	
	public boolean vizinhancaSegura() {
		return vizinhos.stream().noneMatch(v -> v.minado);
	}
	
	public boolean isMarcado() {
		return marcado;
	}
	
	void setAberto(boolean aberto) {
		this.aberto = aberto;
		if(aberto) {
			notificarObservadores(CampoEvento.ABRIR);
		}
	}
	
	public boolean isAberto() {
		return aberto;
	}
	
	public boolean isFechado() {
		return !isAberto();
	}

	public int getColuna() {
		return coluna;
	}

	public int getLinha() {
		return linha;
	}
	
	boolean objetivoAlcancado() {
		boolean desvendado = !minado && aberto;
		boolean protegido = minado && marcado;
		return desvendado || protegido;
	}
	
	public int minasNaVizinhanca() {
		return (int) vizinhos.stream().filter(v -> v.minado).count();
	}
	
	void reiniciar() {
		aberto = false;
		minado = false;
		marcado = false;
	}
	
	


}
