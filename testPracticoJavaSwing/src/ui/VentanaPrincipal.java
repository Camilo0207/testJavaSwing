package ui;

import model.Tarea;
import repository.TareaRepository;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private TareaRepository repositorio;
    private JTextField txtTitulo;
    private JTextArea txtDescripcion;
    private JComboBox<String> comboEstado;
    private JTable tablaTareas;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar, btnEliminar, btnCambiarEstado;

    public VentanaPrincipal() {
        this.repositorio = new TareaRepository();
        configurarVentana();
        inicializarComponentes();
        configurarEventos();
        actualizarTabla();
    }

    private void configurarVentana() {
        setTitle("Gestor de Tareas To-Do");
        setSize(1000, 550); // Un poco más ancha para los nuevos botones
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
    }

    private void inicializarComponentes() {
        // --- PANEL IZQUIERDO: FORMULARIO ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Formulario para nueva tarea"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTitulo = new JTextField(20);
        txtDescripcion = new JTextArea(6, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        comboEstado = new JComboBox<>(new String[]{"Pendiente", "En Progreso", "Completada"});

        btnGuardar = new JButton("Guardar Tarea");
        btnGuardar.setBackground(new Color(40, 167, 69));
        btnGuardar.setForeground(Color.BLACK); // Blanco para mejor contraste
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 12));

        gbc.gridx = 0; gbc.gridy = 0; panelFormulario.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1; panelFormulario.add(txtTitulo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelFormulario.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; panelFormulario.add(new JScrollPane(txtDescripcion), gbc);

        gbc.gridx = 0; gbc.gridy = 2; panelFormulario.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; panelFormulario.add(comboEstado, gbc);

        gbc.gridx = 1; gbc.gridy = 3; panelFormulario.add(btnGuardar, gbc);

        // --- PANEL CENTRAL: TABLA ---
        String[] columnas = {"Título", "Descripción", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura
            }
        };

        tablaTareas = new JTable(modeloTabla);
        tablaTareas.setRowHeight(25); // Filas más altas para legibilidad
        JScrollPane scrollTabla = new JScrollPane(tablaTareas);


        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        btnCambiarEstado = new JButton("Cambiar Estado");
        btnCambiarEstado.setBackground(new Color(0, 123, 255));
        btnCambiarEstado.setForeground(Color.BLACK);

        btnEliminar = new JButton("Eliminar Seleccionada");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.BLACK);

        panelAcciones.add(btnCambiarEstado);
        panelAcciones.add(btnEliminar);

        // AGREGAR AL FRAME
        add(panelFormulario, BorderLayout.WEST);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        // EVENTO: GUARDAR
        btnGuardar.addActionListener(e -> {
            String titulo = txtTitulo.getText().trim();
            String desc = txtDescripcion.getText().trim();
            String estado = (String) comboEstado.getSelectedItem();

            if (!titulo.isEmpty()) {
                Tarea nuevaTarea = new Tarea(titulo, desc, estado);
                repositorio.agregar(nuevaTarea);
                limpiarFormulario();
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "El título es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // EVENTO: CAMBIAR ESTADO
        btnCambiarEstado.addActionListener(e -> {
            int filaSeleccionada = tablaTareas.getSelectedRow();

            if (filaSeleccionada != -1) {
                String[] opciones = {"Pendiente", "En Progreso", "Completada"};

                String nuevoEstado = (String) JOptionPane.showInputDialog(
                        this,
                        "Selecciona el nuevo estado de la tarea:",
                        "Actualizar Estado",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );

                if (nuevoEstado != null) {
                    repositorio.actualizarEstado(filaSeleccionada, nuevoEstado);
                    actualizarTabla();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una tarea de la tabla.");
            }
        });

        // EVENTO: ELIMINAR
        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaTareas.getSelectedRow();

            if (filaSeleccionada != -1) {
                int respuesta = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar esta tarea?");
                if (respuesta == JOptionPane.YES_OPTION) {
                    repositorio.eliminar(filaSeleccionada);
                    actualizarTabla();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una tarea de la tabla.");
            }
        });
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Tarea> lista = repositorio.obtenerTodas();

        for (Tarea t : lista) {
            // Iconos simples para identificar el estado rápido
            String estadoLabel = t.getEstado();
            if (estadoLabel.equalsIgnoreCase("Completada")) estadoLabel = "✅ " + estadoLabel;
            else if (estadoLabel.equalsIgnoreCase("En Progreso")) estadoLabel = "⌛ " + estadoLabel;
            else estadoLabel = "📌 " + estadoLabel;

            Object[] fila = { t.getTitulo(), t.getDescripcion(), estadoLabel };
            modeloTabla.addRow(fila);
        }
    }

    private void limpiarFormulario() {
        txtTitulo.setText("");
        txtDescripcion.setText("");
        comboEstado.setSelectedIndex(0);
        txtTitulo.requestFocus();
    }
}