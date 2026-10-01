package com.gyan.pack3;


import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.security.*;


public class QuizAppPro extends JFrame {

    static Connection con;
    static String currentUser;

   
    static void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quiz_app",
                "root",
                "123456"
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    
    static JButton createBtn(String text) {
        JButton btn = new JButton(text);

        btn.setFocusPainted(false);
        btn.setBackground(new Color(52, 73, 94));   
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(41, 128, 185)); 
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94));
            }
        });

        return btn;
    }
    
    
    
    
  
        
    

    // FOR LOGIN
    static void loginUI() {

        JFrame f = new JFrame("Quiz Login");
        f.setSize(900, 600);
        f.setLocationRelativeTo(null);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //  MAIN BACKGROUND 
        JPanel bg = new JPanel();
        bg.setBackground(new Color(0, 0, 0));
        bg.setLayout(new GridBagLayout());

        //  CARD PANEL 
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(600, 400));
        card.setBackground(Color.CYAN);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createLineBorder(new Color(200,200,200),1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //  TITLE 
        JLabel title = new JLabel("Java programming quiz", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(255, 0, 0));

        //  INPUTS 
        JLabel l1 = new JLabel("Username");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel l2 = new JLabel("Password");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();

        user.setPreferredSize(new Dimension(200, 30));
        pass.setPreferredSize(new Dimension(200, 30));

        //  BUTTONS 
        JButton login = new JButton("Login");
        JButton signup = new JButton("Signup");

        login.setBackground(new Color(0, 120, 215));
        login.setForeground(Color.BLACK);
        login.setFocusPainted(false);

        signup.setBackground(new Color(0, 120, 215));
        signup.setForeground(Color.BLACK);
        signup.setFocusPainted(false);

        //  LAYOUT 
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(l1, gbc);

        gbc.gridx = 1;
        card.add(user, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(l2, gbc);

        gbc.gridx = 1;
        card.add(pass, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        card.add(login, gbc);

        gbc.gridx = 1;
        card.add(signup, gbc);

        bg.add(card);
        f.add(bg);

        //  ACTION 
        login.addActionListener(e -> {
            try {
                // 1. CONNECT DATABASE (THIS FIXES YOUR ERROR)
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/quiz_app", 
                        "root",                               
                        "123456"                                
                );

                // 2. GET INPUT
                String username = user.getText();
                String password = new String(pass.getPassword());

                // 3. QUERY
                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM users WHERE username=? AND password=?"
                );
                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();

                // 4. CHECK LOGIN
                if (rs.next()) {
                    String role = rs.getString("role");

                    JOptionPane.showMessageDialog(null, "Login Success");

                    f.dispose(); 

                    if (role.equals("admin")) {
                        f.dispose(); 
                        adminUI();
                    } else {
                        f.dispose(); 
                        currentUser = username; 
                        userHome(); 
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Username or Password");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
        

        signup.addActionListener(e -> {
            try {
                String username = user.getText();
                String password = new String(pass.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(f, "Enter username and password");
                    return;
                }

                PreparedStatement check = con.prepareStatement(
                    "SELECT * FROM users WHERE username=?"
                );
                check.setString(1, username);

                ResultSet rs1 = check.executeQuery();

                if (rs1.next()) {
                    JOptionPane.showMessageDialog(f, "Username already exists");
                    return;
                }

                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users (username, password, role) VALUES (?,?,?)"
                );

                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, "user");

                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "Signup Success");

                user.setText("");
                pass.setText("");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(f, ex.getMessage());
            }
        });

        
        f.setVisible(true);
    }
    
   
    //  ADMIN PANEL
    static void adminUI() {

        JFrame frame = new JFrame("University Quiz Admin Panel");
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        //  HEADER 
        JPanel header = new JPanel();
        header.setBackground(new Color(255, 0, 0));
        header.setPreferredSize(new Dimension(1000, 60));

        JLabel title = new JLabel("University Quiz Admin Panel");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title);

        // ............ SIDEBAR 
        JPanel sidebar = new JPanel(new GridLayout(6,1,10,15));
        sidebar.setBackground(new Color(70,130,180));
        sidebar.setPreferredSize(new Dimension(200,600));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));

        JButton home = createBtn("Home");
        home.setBackground(new Color(0,0,0));
        JButton addQ = createBtn("Add Question");
        addQ.setBackground(new Color(0,0,0));
        JButton manageQ = createBtn("Manage Questions");
        manageQ.setBackground(new Color(0,0,0));
        JButton leaderboard = createBtn("Leaderboard");
        leaderboard.setBackground(new Color(0,0,0));
        JButton logout = createBtn("Logout");
        logout.setBackground(new Color(0,0,0));

        sidebar.add(home);
        sidebar.add(addQ);
        sidebar.add(manageQ);
        sidebar.add(leaderboard);
        sidebar.add(new JLabel(""));
        sidebar.add(logout);

        //  CONTENT 
        JPanel content = new JPanel(new CardLayout());
        content.setBackground(new Color(245,247,250));

        //  HOME 
        JPanel homePanel = new JPanel(new GridBagLayout());
        homePanel.setBackground(new Color(30,30,30));

        JLabel welcome = new JLabel("Welcome Admin");
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 30));
        homePanel.add(welcome);

        // ADD QUESTION 
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBackground(new Color(245,247,250));

        JPanel form = new JPanel(new GridLayout(7,2,15,15));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(30,40,30,40));

        JTextField q = new JTextField();
        JTextField a = new JTextField();
        JTextField b = new JTextField();
        JTextField c = new JTextField();
        JTextField d = new JTextField();
        JTextField correct = new JTextField();

        form.add(new JLabel("Question")); form.add(q);
        form.add(new JLabel("Option A")); form.add(a);
        form.add(new JLabel("Option B")); form.add(b);
        form.add(new JLabel("Option C")); form.add(c);
        form.add(new JLabel("Option D")); form.add(d);
        form.add(new JLabel("Correct (A/B/C/D)")); form.add(correct);

        JButton save = new JButton("Save");
        save.setBackground(new Color(0,123,255));
        save.setForeground(Color.WHITE);

        form.add(new JLabel(""));
        form.add(save);

        addPanel.add(form);

        // SAVE QUESTION
        save.addActionListener(e -> {
            try {
                PreparedStatement ps = con.prepareStatement(
               	"INSERT INTO questions (question, optionA, optionB, optionC, optionD, correct) VALUES (?,?,?,?,?,?)"
                		);
                ps.setString(1, q.getText());
                ps.setString(2, a.getText());
                ps.setString(3, b.getText());
                ps.setString(4, c.getText());
                ps.setString(5, d.getText());
                ps.setString(6, 
                correct.getText().toUpperCase());
                
                ps.executeUpdate();
                
                q.setText("");
                a.setText("");
                b.setText("");
                c.setText("");
                d.setText("");
                correct.setText("");

                JOptionPane.showMessageDialog(frame, "Question Added");
                
                manageQ.doClick();
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        //  FOR MANAGE QUESTIONS 
        JPanel managePanel = new JPanel(new BorderLayout());
        managePanel.setBackground(new Color(245,247,250));

        String[] colsQ = {"ID","Question","A","B","C","D","Correct"};
        DefaultTableModel modelQ = new DefaultTableModel(colsQ,0);
        JTable tableQ = new JTable(modelQ);

        JScrollPane spQ = new JScrollPane(tableQ);
        spQ.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(Color.RED);
        deleteBtn.setForeground(Color.WHITE);

        managePanel.add(spQ, BorderLayout.CENTER);
        managePanel.add(deleteBtn, BorderLayout.SOUTH);

        // LOAD QUESTIONS
        manageQ.addActionListener(e -> {
            modelQ.setRowCount(0);
            try {
                ResultSet rs = con.createStatement().executeQuery("SELECT * FROM questions");

                int id = 1;
                while(rs.next()){
                    modelQ.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("question"),   
                        rs.getString("optionA"),
                        rs.getString("optionB"),
                        rs.getString("optionC"),
                        rs.getString("optionD"),
                        rs.getString("correct")
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // DELETE QUESTION
        deleteBtn.addActionListener(e -> {
            int row = tableQ.getSelectedRow();

            if(row == -1){
                JOptionPane.showMessageDialog(null, "Select a row first");
                return;
            }

            String question = modelQ.getValueAt(row,1).toString();

            try {
                PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM questions WHERE question=?"
                );
                ps.setString(1, question);
                ps.executeUpdate();

                modelQ.removeRow(row);
                JOptionPane.showMessageDialog(null, "Deleted Successfully");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        //  LEADERBOARD 
        JPanel leaderPanel = new JPanel(new BorderLayout());
        leaderPanel.setBackground(new Color(245,247,250));

        String[] cols = {"Username", "Score", "Total"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        leaderPanel.add(sp, BorderLayout.CENTER);

        leaderboard.addActionListener(e -> {

            //  IMPORTANT: SHOW PANEL
            ((CardLayout) content.getLayout()).show(content, "LEADER");

            model.setRowCount(0); 

            try {
                ResultSet rs = con.createStatement().executeQuery(
                    "SELECT username, score, total FROM results ORDER BY score DESC"
                );

                while(rs.next()){
                    model.addRow(new Object[]{
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getInt("total")
                    });
                }

            } catch(Exception ex){
                ex.printStackTrace();
            }
        });

        //  ADD PANELS 
        content.add(homePanel, "home");
        content.add(addPanel, "add");
        content.add(managePanel, "manage");
        content.add(leaderPanel, "leader");

        CardLayout cl = (CardLayout) content.getLayout();

        home.addActionListener(e -> cl.show(content, "home"));
        addQ.addActionListener(e -> cl.show(content, "add"));
        manageQ.addActionListener(e -> cl.show(content, "manage"));
        leaderboard.addActionListener(e -> cl.show(content, "leader"));

        logout.addActionListener(e -> {
            frame.dispose();
            loginUI();
        });

        frame.add(header, BorderLayout.NORTH);
        frame.add(sidebar, BorderLayout.WEST);
        frame.add(content, BorderLayout.CENTER);

        frame.setVisible(true);
    }
    
   
    //  USER HOME 
    static void userHome() {

        JFrame f = new JFrame("User Dashboard");
        f.setSize(900, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);

        //  MAIN PANEL (BACKGROUND) 
        JPanel bg = new JPanel(new BorderLayout());
        bg.setBackground(new Color(30, 60, 120)); 

        //  CARD PANEL 
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400, 350));
        card.setBackground(Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //  TITLE 
        JLabel title = new JLabel("Java programming quiz");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(title, gbc);

        //  BUTTONS 
        JButton quiz = new JButton("Start Quiz");
        JButton leader = new JButton("Leaderboard");
        JButton logout = new JButton("Logout");
        quiz.setPreferredSize(new Dimension(200, 40));
        leader.setPreferredSize(new Dimension(200, 40));
        logout.setPreferredSize(new Dimension(200, 40));

        // Style buttons
        Font btnFont = new Font("Arial", Font.BOLD, 16);
        quiz.setFont(btnFont);
        leader.setFont(btnFont);
        logout.setFont(btnFont);

        quiz.setBackground(new Color(40, 167, 69)); 
        quiz.setForeground(Color.WHITE);

        leader.setBackground(new Color(23, 162, 184)); 
        leader.setForeground(Color.WHITE);

        logout.setBackground(new Color(220, 53, 69)); 
        logout.setForeground(Color.WHITE);

        //  ADD BUTTONS 
        gbc.gridy = 1;
        card.add(quiz, gbc);

        gbc.gridy = 2;
        card.add(leader, gbc);

        gbc.gridy = 3;
        card.add(logout, gbc);

        //  ADD CARD TO BG 
        bg.add(card);

        f.add(bg);
        f.setVisible(true);

        //  ACTIONS 
        quiz.addActionListener(e -> {
            f.dispose();   
            quizUI();      
        });
        leader.addActionListener(e -> {
           
            showLeaderboard(bg);   
        });
 
        logout.addActionListener(e -> {
            f.dispose();
            loginUI();
        });
    }

    
    //  QUIZ (FINAL EXAM STYLE) 
    static void quizUI() {

        java.util.List<String[]> qs = new ArrayList<>();

        try {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM questions");
            while (rs.next()) {
                qs.add(new String[]{
                        rs.getString("question"),
                        rs.getString("optionA"),
                        rs.getString("optionB"),
                        rs.getString("optionC"),
                        rs.getString("optionD"),
                        rs.getString("correct")
                });
            }
        } catch (Exception e) {}

        int totalQ = qs.size();
        int totalTime = totalQ * 60;

        JFrame f = new JFrame("Exam");
        f.setSize(800, 500);
        f.setLayout(null);
        f.setLocationRelativeTo(null);

        JLabel qLabel = new JLabel();
        JLabel timerLabel = new JLabel();

        JRadioButton[] opt = new JRadioButton[4];
        ButtonGroup bg = new ButtonGroup();

        JButton next = new JButton("Next");
        JButton prev = new JButton("Prev");
        JButton mark = new JButton("Mark Review");
        JButton submit = new JButton("Submit");

        JPanel palette = new JPanel(new GridLayout(5, 6, 5, 5));
        palette.setBounds(500, 50, 250, 300);

        JButton[] qBtns = new JButton[totalQ];

        int[] selected = new int[totalQ];
        boolean[] review = new boolean[totalQ];

        Arrays.fill(selected, -1);

        qLabel.setBounds(50, 50, 400, 30);
        timerLabel.setBounds(600, 10, 150, 30);

        for (int i = 0; i < 4; i++) {
            opt[i] = new JRadioButton();
            opt[i].setBounds(50, 100 + i * 40, 300, 30);
            bg.add(opt[i]);
            f.add(opt[i]);
        }

        final int[] index = {0};
        final int[] time = {totalTime};

        for (int i = 0; i < totalQ; i++) {
            int idx = i;
            qBtns[i] = new JButton("" + (i + 1));
            qBtns[i].setBackground(Color.WHITE);

            qBtns[i].addActionListener(e -> {
                index[0] = idx;
                load(qs, opt, bg, selected, index, qLabel, qBtns, review);
            });

            palette.add(qBtns[i]);
        }

        prev.setBounds(50, 300, 100, 30);
        next.setBounds(160, 300, 100, 30);
        mark.setBounds(270, 300, 120, 30);
        submit.setBounds(400, 300, 100, 30);

        f.add(qLabel);
        f.add(timerLabel);
        f.add(prev);
        f.add(next);
        f.add(mark);
        f.add(submit);
        f.add(palette);

        Timer timer = new Timer(1000, e -> {
            time[0]--;

            int min = time[0] / 60;
            int sec = time[0] % 60;

            timerLabel.setText("Time: " + min + ":" + sec);

            if (time[0] <= 0) {
                ((Timer) e.getSource()).stop();
                submitQuiz(qs, selected, f);
            }
        });

        timer.start();

        next.addActionListener(e -> {
            save(opt, selected, index, qBtns);
            if (index[0] < totalQ - 1) {
                index[0]++;
                load(qs, opt, bg, selected, index, qLabel, qBtns, review);
            }
        });

        prev.addActionListener(e -> {
            save(opt, selected, index, qBtns);
            if (index[0] > 0) {
                index[0]--;
                load(qs, opt, bg, selected, index, qLabel, qBtns, review);
            }
        });

        mark.addActionListener(e -> {
            review[index[0]] = true;
            qBtns[index[0]].setBackground(Color.YELLOW);
        });

        submit.addActionListener(e -> {
            save(opt, selected, index, qBtns);
            submitQuiz(qs, selected, f);
        });

        load(qs, opt, bg, selected, index, qLabel, qBtns, review);

        f.setVisible(true);
    }
    
    
    static void save(JRadioButton[] opt, int[] selected, int[] index, JButton[] qBtns) {
        for (int i = 0; i < 4; i++) {
            if (opt[i].isSelected()) {
                selected[index[0]] = i;
                qBtns[index[0]].setBackground(Color.GREEN);
            }
        }
    }
    
    
    static void load(java.util.List<String[]> qs, JRadioButton[] opt, ButtonGroup bg,
            int[] selected, int[] index, JLabel qLabel, JButton[] qBtns, boolean[] review) {

  bg.clearSelection();

  String[] q = qs.get(index[0]);
  qLabel.setText("Q" + (index[0] + 1) + ": " + q[0]);

  for (int i = 0; i < 4; i++) {
    opt[i].setText(q[i + 1]);

    if (selected[index[0]] == i) {
        opt[i].setSelected(true);
    }
 }

    if (review[index[0]]) {
       qBtns[index[0]].setBackground(Color.YELLOW);
  } else if (selected[index[0]] != -1) {
      qBtns[index[0]].setBackground(Color.GREEN);
  }   else {
        qBtns[index[0]].setBackground(Color.WHITE);
   }
 }   


    static void submitQuiz(java.util.List<String[]> qs, int[] selected, JFrame f) {
        int score = 0;

        for (int i = 0; i < qs.size(); i++) {
            String correct = qs.get(i)[5];

            if (selected[i] != -1 &&
                correct.equalsIgnoreCase(String.valueOf((char) ('A' + selected[i])))) {
                score++;
            }
        }
        System.out.println("TOTAL QUESTION = " + qs.size());

        JOptionPane.showMessageDialog(f, "Your Score: " + score);
        try {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO results(username, score , total) VALUES (?, ? , ?)"
            );
            ps.setString(1, currentUser);   
            ps.setInt(2, score);
            ps.setInt(3, qs.size());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ IMPORTANT PART
        f.dispose();       
        userHome();        
    }
   
    
    
	//  LEADERBOARD 
    static void showLeaderboard(JPanel parent) {

        parent.removeAll();
        parent.setLayout(new BorderLayout());

        //  TOP PANEL (TITLE + BACK BUTTON) 
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 60, 120));

        JLabel title = new JLabel("Leaderboard", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JButton back = new JButton("← Back");
        back.setFocusPainted(false);
        back.setBackground(new Color(220, 53, 69));   
        back.setForeground(Color.WHITE);
        back.setFont(new Font("Arial", Font.BOLD, 14));

        topPanel.add(back, BorderLayout.WEST);
        topPanel.add(title, BorderLayout.CENTER);

        //  TABLE 
        String[] columns = {"Rank", "User", "Score"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try {
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT username, score FROM results ORDER BY score DESC"
            );

            int rank = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                    rank++,
                    rs.getString("username"),
                    rs.getInt("score")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        // Header style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(0, 102, 204));
        header.setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);

        //  ADD TO PANEL 
        parent.add(topPanel, BorderLayout.NORTH);
        parent.add(scroll, BorderLayout.CENTER);

        parent.revalidate();
        parent.repaint();

        //  BACK BUTTON ACTION 
        back.addActionListener(e -> {
            parent.removeAll();
            userHome();   
        });
    }

    //  MAIN 
    public static void main(String[] args) {
        connectDB();
        loginUI();
    }
}
