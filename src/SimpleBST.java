import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.BiConsumer;

/**
 * A simple implementation of binary search trees.
 */
@SuppressWarnings("unused")
public class SimpleBST<K, V> implements SimpleMap<K, V> {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The root of our tree. Initialized to null for an empty tree.
   */
  BSTNode<K, V> root;

  /**
   * The comparator used to determine the ordering in the tree.
   */
  Comparator<? super K> comparator;

  /**
   * The size of the tree.
   */
  int size;

  /**
   * A cached value (useful in some circumstances.
   */
  V cachedValue;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new binary search tree that orders values using the
   * specified comparator.
   */
  public SimpleBST(Comparator<? super K> comparator) {
    this.comparator = comparator;
    this.root = null;
    this.size = 0;
    this.cachedValue = null;
  } // SimpleBST(Comparator<K>)

  /**
   * Create a new binary search tree that orders values using a
   * not-very-clever default comparator.
   */
  public SimpleBST() {
    //noinspection ComparatorCombinators
    this((k1, k2) -> k1.toString().compareTo(k2.toString()));
  } // SimpleBST()


  // +-------------------+-------------------------------------------
  // | SimpleMap methods |
  // +-------------------+

  public V setRecursively(K key, V value) {
    if (key == null) {
      throw new NullPointerException();
    } // if
    this.root = setRecursively(key, value, this.root);
    return cachedValue;
  } // set(K,V)

  private BSTNode<K, V> setRecursively(K key, V value, BSTNode<K, V> node) {
    if (node == null) {
      cachedValue = null;
      this.size++;
      return new BSTNode<>(key, value);
    } // if
    int difference = key.toString().compareTo(node.key.toString());
    if (difference < 0) {
      node.left = setRecursively(key, value, node.left);
    } else if (difference > 0) {
      node.right = setRecursively(key, value, node.right);
    } else {
      cachedValue = node.value;
      node.value = value;
    } // if/else
    return node;
  } // set(K,V)

  @Override
  public V set(K key, V value) {
    if (key == null) {
      throw new NullPointerException();
    } // if
    BSTNode<K, V> node = this.root;
    while (node != null) {
      int difference = key.toString().compareTo(node.key.toString());
      if (difference < 0) {
        if (node.left == null) {
          node.left = new BSTNode<>(key, value);
          return null;
        } else {
          node = node.left;
        } // if/else
      } else if (difference > 0) {
        if (node.right == null) {
          node.right = new BSTNode<>(key, value);
          return null;
        } else {
          node = node.right;
        } // if/else
      } else {
        cachedValue = node.value;
        node.value = value;
        return cachedValue;
      } // if/else
    } // while
    this.root = new BSTNode<>(key, value);
    cachedValue = null;
    return null;
  } // set

  @Override
  public V remove(K key) {
    if (key == null) {
      throw new NullPointerException();
    } // if
    this.root = removeRecurse(key, this.root);
    return cachedValue;
  } // remove()

  private BSTNode<K, V> removeRecurse(K key, BSTNode<K, V> node) {
    if (node == null) {
      cachedValue = null;
      return null;
    } // if
    int difference = key.toString().compareTo(node.key.toString());
    if (difference < 0) {
      node.left = removeRecurse(key, node.left);
    } else if (difference > 0) {
      node.right = removeRecurse(key, node.right);
    } else {
      cachedValue = node.value;
      if (node.left == null && node.right == null) {
        return null;
      } else if (node.left == null) {
        node = node.right;
      } else if (node.right == null) {
        node = node.left;
      } else {
        BSTNode<K, V> removedNode = removeRecurse(node.left.key, node.left);
        node.value = removedNode.value;
      } // if/else
      size--;
    } // if/else
    return node;
  } // remove()

  @Override
  public V get(K key) {
    if (key == null) {
      throw new NullPointerException("null key");
    } // if
    return get(key, root);
  } // get(K,V)

  @Override
  public int size() {
    return 0;           // STUB
  } // size()

  @Override
  public boolean containsKey(K key) {
    return false;       // STUB
  } // containsKey(K)

  @Override
  public Iterator<K> keys() {
    return new Iterator<>() {
      final Iterator<BSTNode<K, V>> nit = SimpleBST.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public K next() {
        return nit.next().key;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // keys()

  @Override
  public Iterator<V> values() {
    return new Iterator<>() {
      final Iterator<BSTNode<K, V>> nit = SimpleBST.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public V next() {
        return nit.next().value;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // values()

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Iterator<BSTNode<K, V>> nodes = this.nodes();
    while (nodes.hasNext()) {
      BSTNode<K, V> node = nodes.next();
      action.accept(node.key, node.value);
    } // while
  } // forEach

  // +----------------------+----------------------------------------
  // | Other public methods |
  // +----------------------+

  /**
   * Dump the tree to some output location.
   */
  public void dump(PrintWriter pen) {
    dump(pen, root, "");
  } // dump(PrintWriter)


  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Dump a portion of the tree to some output location.
   */
  void dump(PrintWriter pen, BSTNode<K, V> node, String indent) {
    if (node == null) {
      pen.println(indent + "<>");
    } else {
      pen.println(indent + node.key + ": " + node.value);
      if ((node.left != null) || (node.right != null)) {
        dump(pen, node.left, indent + "  ");
        dump(pen, node.right, indent + "  ");
      } // if it has children
    } // else
  } // dump

  /**
   * Get the value associated with a key in a subtree rooted at node.  See the
   * top-level get for more details.
   */
  V get(K key, BSTNode<K, V> node) {
    if (node == null) {
      throw new IndexOutOfBoundsException("Invalid key: " + key);
    }
    int comp = comparator.compare(key, node.key);
    if (comp == 0) {
      return node.value;
    } else if (comp < 0) {
      return get(key, node.left);
    } else {
      return get(key, node.right);
    }
  } // get(K, BSTNode<K,V>)

  /**
   * Get an iterator for all the nodes. (Useful for implementing the
   * other iterators.)
   */
  Iterator<BSTNode<K, V>> nodes() {
    return new Iterator<BSTNode<K, V>>() {

      final Stack<BSTNode<K, V>> stack = new Stack<>();
      boolean initialized = false;


      @Override
      public boolean hasNext() {
        checkInit();
        return !stack.empty();
      } // hasNext()

      @Override
      public BSTNode<K, V> next() {
        checkInit();
        if (stack.empty()) {
          return null;
        } // if
        return stack.pop();
      } // next();

      void checkInit() {
        if (!initialized) {
          stack.push(SimpleBST.this.root);
          initialized = true;
        } // if
      } // checkInit
    }; // new Iterator
  } // nodes()

} // class SimpleBST
