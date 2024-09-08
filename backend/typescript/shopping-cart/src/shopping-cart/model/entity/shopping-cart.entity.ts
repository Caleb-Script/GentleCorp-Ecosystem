
@Entity()

private UUID id;
    /**
     * Die Versionsnummer des Einkaufswagens.
     */
    @Version
    private int version;
    /**
     * Die Gesamtsumme des Warenkorbs
     */
    @DecimalMin(value = "0.0", message = "Die Gesamtsumme muss größer als 0 sein")
    private BigDecimal totalAmount;
    /**
     * die Kunden-ID
     */
    @NotNull(message = "Die Kunden-ID darf nicht null sein")
    private UUID customerId;
    /**
     * Benutzernamen des Kunden
     */
    @Column(name = "customer_username")
    private String customerUsername;
    /**
     * Status des Warenkorbs,
     * gibt an, ob der Einkaufswagen abgeschlossen ist
     */
    @Column(name = "is_complete")
    private boolean isComplete;

    /**
     * Liste der Artikel im Einkaufswagen.
     */
    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "shopping_cart_id")
    @OrderColumn(name = "idx", nullable = false)
    @ToString.Exclude
    private List<Item> cartItems;

    /**
     * Zeitpunkt der Erstellung des Einkaufswagens.
     */
   @CreationTimestamp
    private LocalDateTime created;
    /**
     * Zeitpunkt der letzten Aktualisierung des Einkaufswagens.
     */
    @UpdateTimestamp
    private LocalDateTime updated;
