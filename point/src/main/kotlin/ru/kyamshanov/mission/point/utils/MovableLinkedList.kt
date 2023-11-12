package ru.kyamshanov.mission.point.utils

class MovableLinkedList<ID : Any>(
    private val initialList: List<ID>
) {

    private val idNodeMap = mutableMapOf<ID, Node<ID>>()
    private var head: ID

    init {
        initialList.forEachIndexed { index, id ->
            add(Node(id = id, previous = initialList.getOrNull(index - 1), next = initialList.getOrNull(index + 1)))
        }
        initialList.first().also {
            head = it
        }
        println(idNodeMap)
    }

    fun add(node: Node<ID>) {
        assert(!idNodeMap.contains(node.id)) { "The element was already added" }
        idNodeMap[node.previous]?.also { assert(it.next == null || it.next == node.id) { "Error in validation previous node $it. Previous node should has next null id or equal ${node.id}" } }
        idNodeMap[node.next]?.also { assert(it.previous == null || it.previous == node.id) { "Error in validation next node $it. Next node should has previous null id or equal ${node.id}" } }
        idNodeMap[node.id] = node
    }

    fun move(id: ID, beforeIndex: ID) {
        val oldNode = requireNotNull(idNodeMap[id])

        //cut
        val p = idNodeMap[oldNode.previous]
        val n = idNodeMap[oldNode.next]
        p?.copy(next = n?.id)?.also { idNodeMap[it.id] = it } ?: run { head = requireNotNull(n).id }
        n?.copy(previous = p?.id)?.also { idNodeMap[it.id] = it }

        val nextNode = requireNotNull(idNodeMap[beforeIndex])
        val oldNextNode = idNodeMap[nextNode.previous]
        oldNextNode?.copy(next = id)?.also { idNodeMap[it.id] = it }
        nextNode.copy(previous = id).also { idNodeMap[it.id] = it }
        oldNode.copy(next = beforeIndex, previous = oldNextNode?.id).also {
            idNodeMap[it.id] = it
            if (oldNextNode == null) head = it.id
        }
    }

    fun getList(): List<ID> = buildList(requireNotNull(idNodeMap[head]))

    private fun buildList(node: Node<ID>, destination: MutableList<ID> = mutableListOf()): List<ID> {
        destination.add(node.id)
        return if (node.next != null) {
            idNodeMap[node.next]?.also { buildList(it, destination) }
            destination
        } else {
            destination
        }
    }

    data class Node<ID : Any>(
        val previous: ID?,
        val id: ID,
        val next: ID?
    ) {

        init {
            if (previous == null && next == null) throw IllegalArgumentException("the Previous and the Next cannot be null together")
        }
    }
}