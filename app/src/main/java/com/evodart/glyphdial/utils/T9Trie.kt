package com.evodart.glyphdial.utils

import com.evodart.glyphdial.data.model.Contact

/**
 * A Trie data structure optimized for fast T9 contact searching.
 * Maps names and numbers to a searchable digit tree.
 */
class T9Trie {
    private class TrieNode {
        val children = mutableMapOf<Char, TrieNode>()
        // Use a set to prevent duplicate contacts in the results
        val contacts = mutableSetOf<Contact>()
    }

    private val root = TrieNode()

    companion object {
        private val charToT9Map = mapOf(
            'a' to '2', 'b' to '2', 'c' to '2',
            'd' to '3', 'e' to '3', 'f' to '3',
            'g' to '4', 'h' to '4', 'i' to '4',
            'j' to '5', 'k' to '5', 'l' to '5',
            'm' to '6', 'n' to '6', 'o' to '6',
            'p' to '7', 'q' to '7', 'r' to '7', 's' to '7',
            't' to '8', 'u' to '8', 'v' to '8',
            'w' to '9', 'x' to '9', 'y' to '9', 'z' to '9'
        )
        
        fun charToT9(c: Char): Char? = charToT9Map[c.lowercaseChar()]
    }

    /**
     * Rebuilds the Trie index from the given contacts list.
     * Should be called on a background thread.
     */
    fun build(contacts: List<Contact>) {
        root.children.clear()
        root.contacts.clear()
        
        for (contact in contacts) {
            // Index Phone Numbers (Allow Substring Search)
            for (phone in contact.phoneNumbers) {
                val digitsOnly = phone.number.filter { it.isDigit() }
                insertSuffixes(digitsOnly, contact)
            }
            
            // Index Name (Allow Prefix/Word Search)
            if (contact.name.isNotBlank()) {
                val words = contact.name.split("\\s+".toRegex()).filter { it.isNotBlank() }
                for (word in words) {
                    val t9Word = word.mapNotNull { charToT9(it) }.joinToString("")
                    if (t9Word.isNotEmpty()) {
                        insertPrefixes(t9Word, contact)
                    }
                }
                
                // Also index the full continuous name to match across word boundaries
                val fullT9Name = contact.name.mapNotNull { if (it.isLetter()) charToT9(it) else null }.joinToString("")
                if (fullT9Name.isNotEmpty()) {
                    insertPrefixes(fullT9Name, contact)
                }
            }
        }
    }

    private fun insertPrefixes(code: String, contact: Contact) {
        var current = root
        for (char in code) {
            current = current.children.getOrPut(char) { TrieNode() }
            current.contacts.add(contact)
        }
    }
    
    private fun insertSuffixes(code: String, contact: Contact) {
        // Allows substring matching on phone numbers natively within the tree
        for (i in code.indices) {
            var current = root
            for (j in i until code.length) {
                val char = code[j]
                current = current.children.getOrPut(char) { TrieNode() }
                current.contacts.add(contact)
            }
        }
    }

    /**
     * Searches for contacts matching the T9 digit query.
     */
    fun search(query: String): List<Contact> {
        val cleanQuery = query.filter { it.isDigit() }
        if (cleanQuery.isEmpty()) return emptyList()
        var current = root
        for (char in cleanQuery) {
            current = current.children[char] ?: return emptyList()
        }
        return current.contacts.toList()
    }
}
