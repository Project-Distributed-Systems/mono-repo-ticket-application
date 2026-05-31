package com.ticket.ticket.domain.event;

import java.util.List;
import java.util.Optional;

// domain/event/EventRepository.java
public interface TicketRepository {
  void save(Ticket ticket);

  Optional<Ticket> findBy(TicketId id);

  List<Ticket> findActiveTicketBy(ID eventId);
}
