package com.gurgaczj;

import com.google.common.collect.LinkedHashMultimap;

import java.util.*;

public class EstDec {

    private EstDecTree estDecTree;

    public EstDec() {
        estDecTree = new EstDecTree();
    }


    public EstDec(Double smin, Double sins, Double sprn) {
        estDecTree = new EstDecTree(smin, sprn, sins);
    }

    void setDecayRate(double b, double h) {
        estDecTree.setDecayRate(b, h);
    }

    public void processTransaction(List<String[]> transaction) {
        estDecTree.updateParam();
        transaction.forEach(itemSet -> {
        estDecTree.updateCount(itemSet);
        estDecTree.insertItemSet(estDecTree.getRoot(), itemSet, 0);
        });
    }

    public Set<FrequentItemset> buildFrequentItemSets() {
        EstDecNode root = getRootNode();

        if (root.getChildrens().isEmpty()) {
            return null;
        }

        //Hashtable<Double, String[]> frequentItemSets = estDecTree.createFrequentItemSets(estDecTree.getRoot(), new String[0], new Hashtable<Double, String[]>());

        LinkedHashMultimap<Double , String[]> itemSetsSet = LinkedHashMultimap.create();
        Set<FrequentItemset> frequentItemsets = new LinkedHashSet<>();
        for (EstDecNode estDecNode : root.getChildrens()) {
            frequentItemsets.addAll(recursiveItemSetBuilding(estDecNode, new String[0], frequentItemsets));
        }

        return frequentItemsets;
    }



    private Set<FrequentItemset> recursiveItemSetBuilding(EstDecNode estDecNode, String[] items, Set<FrequentItemset> itemsSet) {
        estDecNode.updateCountForSelectionPhase(estDecTree.getD(), estDecTree.getK());
        double support = estDecNode.calculateSupport(estDecTree.getDk());
        if(support < estDecTree.getSmin()){
            return itemsSet;
        }
        String[] tempItems = new String[items.length + 1];
        System.arraycopy(items, 0, tempItems, 0, items.length);
        tempItems[items.length] = estDecNode.getItem();
        itemsSet.add(new FrequentItemset(support, tempItems, estDecNode.getError()));
        if (estDecNode.getChildrens().isEmpty()) {
            //itemsSet.add(tempItems);
            return itemsSet;
        }
        for (EstDecNode childNode : estDecNode.getChildrens()) {
            itemsSet.addAll(recursiveItemSetBuilding(childNode, tempItems, itemsSet));
        }
        return itemsSet;
    }

    private Set<String[]> getAllItemSetsFromNode(EstDecNode node, HashSet<String[]> itemSets) {
        String[] itemArray = new String[1];
        HashSet<EstDecNode> nodeChilds = node.getChildrens();
        if (!nodeChilds.isEmpty()) {
            for (EstDecNode estDecNode : nodeChilds) {
                estDecNode.getItem();
            }
        }
        return null;
    }


    // for testing purposes
    public EstDecNode getRootNode() {
        return estDecTree.getRoot();
    }
}
