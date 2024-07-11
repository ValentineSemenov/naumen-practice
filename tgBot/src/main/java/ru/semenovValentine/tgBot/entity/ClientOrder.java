package ru.semenovValentine.tgBot.entity;

import jakarta.persistence.*;

@Entity
public class ClientOrder {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Client client;

    @Column(nullable = false)
    private Integer status;

    @Column(length = 15, precision = 2, nullable = false)
    private Double total;

    public ClientOrder(Client client, Integer status, Double total) {
        this.client = client;
        this.status = status;
        this.total = total;
    }

    public ClientOrder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
