package com.structurizr.dsl.register;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FilesRegister {
  public static class FilesTree {
    public static class FileNode {
      private List<FileNode> children = new ArrayList<>();
      private FileNode parent;
      private File file;
      private UUID id;

      public FileNode(File file) {
        this(null, file, UUID.randomUUID());
      }

      public FileNode(FileNode parent, File file, UUID id) {
        this.parent = parent;
        this.file = file;
        this.id = id;
      }

      public FileNode addChild(FileNode child) {
        if (child == null || children.contains(child)) {
          return null;
        }

        children.add(child);

        return child;
      }

      public void setParent(FileNode parent) {
        this.parent = parent;
      }

      public FileNode getParent() {
        return parent;
      }

      public List<FileNode> getChildren() {
        return children;
      }

      public File getFile() {
        return file;
      }

      public UUID getId() {
        return id;
      }
    }

    private FileNode root;

    public void setRoot(FileNode root) {
      this.root = root;
    }

    public FileNode addChild(FileNode current, FileNode child) {
      child.setParent(current);
      return current.addChild(child);
    }

    public FileNode getRoot() {
      return root;
    }
  }

  private static FilesRegister INSTANCE = new FilesRegister();

  private static FilesRegister CURRENT_REGISTER = INSTANCE;

  private FilesTree tree;

  private FilesRegister parent;
  private FilesRegister child;

  private FilesTree.FileNode currentNode;

  public static void setRoot(FilesTree.FileNode root) {
    CURRENT_REGISTER.tree.setRoot(root);
    CURRENT_REGISTER.currentNode = root;
  }

  public static void reset() {
    INSTANCE.tree = new FilesTree();
    INSTANCE.parent = null;
    INSTANCE.child = null;
    INSTANCE.currentNode = null;
    CURRENT_REGISTER = INSTANCE;
  }

  public static void reset_current() {
    CURRENT_REGISTER.tree = new FilesTree();
    CURRENT_REGISTER.parent = null;
    CURRENT_REGISTER.child = null;
    CURRENT_REGISTER.currentNode = null;
  }

  public static void addFileAndStartContext(File file) {
    FilesTree.FileNode node = CURRENT_REGISTER.tree.addChild(CURRENT_REGISTER.currentNode, new FilesTree.FileNode(file));
    CURRENT_REGISTER.currentNode = node;
  }

  public static void popContext() {
    if (CURRENT_REGISTER.currentNode.getParent() == null) {
      throw new RuntimeException("Unable to pop context from root element");
    }

    CURRENT_REGISTER.currentNode = CURRENT_REGISTER.currentNode.getParent();
  }

  public static FilesTree.FileNode getCurrentNode() {
    return CURRENT_REGISTER.currentNode;
  }

  public static FilesRegister getCurrentRegister() {
    return CURRENT_REGISTER;
  }

  public static FilesRegister getRootRegister() {
    return INSTANCE;
  }

  public FilesRegister getParent() {
    return parent;
  }

  public FilesTree getFilesTree() {
    return tree;
  }

  public static FilesRegister startParentRegister() {
    FilesRegister parent = new FilesRegister();
    CURRENT_REGISTER.parent = parent;

    FilesRegister current = CURRENT_REGISTER;
    CURRENT_REGISTER = parent;
    reset_current();
    CURRENT_REGISTER.child = current;

    return CURRENT_REGISTER;
  }

  public static void popRegister() {
    if (CURRENT_REGISTER == null || CURRENT_REGISTER.child == null) {
      throw new RuntimeException("There is no child file register");
    }

    CURRENT_REGISTER = CURRENT_REGISTER.child;
  }
}
