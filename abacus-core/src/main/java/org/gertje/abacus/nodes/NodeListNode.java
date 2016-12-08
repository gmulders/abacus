package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Abstract node that represents a list of statements.
 */
public abstract class NodeListNode<T extends Node> extends AbstractNode implements List<T> {

	List<T> nodeList;

	// Constructor.
	public NodeListNode(Token token) {
		super(token);
		// Maak een lijst aan om de objecten op te slaan.
		nodeList = new ArrayList<>();
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
