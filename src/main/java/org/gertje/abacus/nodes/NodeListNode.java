package org.gertje.abacus.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.EvaluationException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTable;

/**
 * Abstracte klasse die een lijst van Nodes voorstelt.
 */
public abstract class NodeListNode<T extends AbstractNode> extends AbstractNode implements List<T> {

	List<T> nodeList;

	// Constructor.
	public NodeListNode(Token token, NodeFactory nodeFactory) {
		super(0, token, nodeFactory);
		// Maak een lijst aan om de objecten op te slaan.
		nodeList = new ArrayList<T>();
	}

	@Override
	public AbstractNode analyse(SymbolTable sym) throws AnalyserException {
		// Loop over de lijst heen om alle nodes in de lijst te analyseren.
		for (int i = 0; i < nodeList.size(); i++) {
			// LET OP! De nodeList kan alleen objecten bevatten van het type T of een type dat T extends. Dit betekent
			// dat we ervoor moeten zorgen dat wanneer we een node van het type T analyseren ook weer een node van dit
			// type terug krijgen.
			nodeList.set(i, (T)nodeList.get(i).analyse(sym));
		}

		// Geef altijd this terug.
		return this;
	}

	@Override
	public Object evaluate(SymbolTable sym) throws EvaluationException {
		// Evalueer alle AbstractNodes en geef het resultaat van de laatste node terug.
		Object result = null;
		for (T node : nodeList) {
			result = node.evaluate(sym);
		}

		return result;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public Class<?> getType() {
		// We geven het resultaat van de laatste geevalueerde node terug, dus we moeten ook het type van deze node terug
		// geven.
		return nodeList.get(nodeList.size() - 1).getType();
	}

	@Override
	public boolean add(T arg0) {
		return nodeList.add(arg0);
	}

	@Override
	public void add(int arg0, T arg1) {
		nodeList.add(arg0, arg1);
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		return nodeList.addAll(arg0);
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1) {
		return nodeList.addAll(arg0, arg1);
	}

	@Override
	public void clear() {
		nodeList.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return nodeList.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return nodeList.containsAll(arg0);
	}

	@Override
	public T get(int arg0) {
		return nodeList.get(arg0);
	}

	@Override
	public int indexOf(Object arg0) {
		return nodeList.indexOf(arg0);
	}

	@Override
	public boolean isEmpty() {
		return nodeList.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return nodeList.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		return nodeList.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<T> listIterator() {
		return nodeList.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int arg0) {
		return nodeList.listIterator(arg0);
	}

	@Override
	public boolean remove(Object arg0) {
		return nodeList.remove(arg0);
	}

	@Override
	public T remove(int arg0) {
		return nodeList.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return nodeList.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return nodeList.retainAll(arg0);
	}

	@Override
	public T set(int arg0, T arg1) {
		return nodeList.set(arg0, arg1);
	}

	@Override
	public int size() {
		return nodeList.size();
	}

	@Override
	public List<T> subList(int arg0, int arg1) {
		return nodeList.subList(arg0, arg1);
	}

	@Override
	public Object[] toArray() {
		return nodeList.toArray();
	}

	@Override
	public <U> U[] toArray(U[] arg0) {
		return nodeList.toArray(arg0);
	}
}
