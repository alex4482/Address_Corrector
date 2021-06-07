package edu.pa.address_corrector.node;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


/**
 * this class is used to represend a node
 * a node location has a parent, except when it represents a Country (parent is null),
 * and an official name
 * from a location node you can always find what the names of all administrative locations above it is,
 * and thus the country or other names.
 */
@ToString
@EqualsAndHashCode
@Setter
@Getter
public class Node implements Serializable {
    private Node parent = null;
    private String name = null;

    public Node() {
    }

    public Node(Node p, String name) {
        parent = p;
        this.name = name;
    }
}
