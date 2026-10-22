// Bibliotecas do REACT
import { BrowserRouter, Routes, Route, Link, Outlet } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Páginas do Sistema
import Laboratorios from './pages/Laboratorios';
import PrincipiosAtivos from './pages/PrincipiosAtivos';
import Composicoes from './pages/Composicoes';
import Formulas from './pages/Formulas';
import CadastroProduto from './pages/CadastroProduto';
import ConsultaProdutos from './pages/ConsultaProdutos';
import Precos from './pages/Precos';
import EntradaEstoque from './pages/EntradaEstoque';
import ConsultaEstoque from './pages/ConsultaEstoque';
import AjustesEstoque from './pages/AjustesEstoque';
import AlertasEstoque from './pages/AlertasEstoque';
import MovimentacoesEstoque from './pages/MovimentacoesEstoque';
import CaixaVenda from './pages/CaixaVenda';
import HistoricoVendas from './pages/HistoricoVendas';
import ResumoVendas from './pages/ResumoVendas';

// ==========================================
// 1. LAYOUT PRINCIPAL (O esqueleto da tela)
// ==========================================
function MainLayout() {
  return (
    <div className="app-container">
      {/* Container invisível que renderiza as notificações (Toasts) */}
      <ToastContainer 
        position="bottom-right" // Pode mudar para "top-right", "top-center", etc.
        autoClose={3000} // Desaparece em 3 segundos
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        pauseOnHover
        theme="colored" // Deixa o fundo verde(sucesso) ou vermelho(erro)
      />
      {/* Menu Superior Horizontal */}
      <nav className="top-navbar">
        <h2>💊 Dr.StoreSys</h2>
        <Link to="/">Início</Link>
        <Link to="/produtos">Gestão de Produto</Link>
        <Link to="/precos">Gestão de Preço</Link>
        <Link to="/vendas">Gestão de Venda</Link>
        <Link to="/estoque">Gestão de Estoque</Link>
      </nav>

      {/* O Outlet é onde as telas filhas vão ser renderizadas */}
      <Outlet /> 
    </div>
  );
}

// ==========================================
// 2. LAYOUT DO MÓDULO DE PRODUTO (Com Sidebar)
// ==========================================
function ProdutoLayout() {
  return (
    <div className="main-body">
      {/* Menu Lateral Vertical exclusivo de Produtos */}
      <aside className="sidebar">
        <h3>Submenus</h3>
        <Link to="/produtos/laboratorios">Laboratórios</Link>
        <Link to="/produtos/principios-ativos">Princípios Ativos</Link>
        <Link to="/produtos/composicoes">Composições</Link>
        <Link to="/produtos/formulas">Fórmulas</Link>
        <hr />
        <Link to="/produtos/cadastro-produtos">Cadastro de Produtos</Link>
        <Link to="/produtos/consulta-produtos">Consulta de Produtos</Link>
      </aside>

      {/* A área onde os formulários vão aparecer */}
      <main className="content-area">
        <Outlet />
      </main>
    </div>
  );
}

// ==========================================
// 2.1 LAYOUT DO MÓDULO DE ESTOQUE (Com Sidebar)
// ==========================================
function EstoqueLayout() {
  return (
    <div className="main-body">
      <aside className="sidebar">
        <h3>Submenus</h3>
        <Link to="/estoque/entrada">Entrada de Mercadoria</Link>
        <Link to="/estoque/consulta">Consulta de Estoque</Link>
        <Link to="/estoque/ajustes">Ajustes e Perdas</Link>
        <hr />
        <Link to="/estoque/alertas">Alertas</Link>
        <Link to="/estoque/movimentacoes">Movimentações</Link>
      </aside>

      <main className="content-area">
        <Outlet />
      </main>
    </div>
  );
}

// ==========================================
// 2.2 LAYOUT DO MÓDULO DE VENDAS (Com Sidebar)
// ==========================================
function VendaLayout() {
  return (
    <div className="main-body">
      <aside className="sidebar">
        <h3>Submenus</h3>
        <Link to="/vendas/caixa">Caixa (PDV)</Link>
        <hr />
        <Link to="/vendas/historico">Histórico de Vendas</Link>
        <Link to="/vendas/resumo">Resumo do Dia</Link>
      </aside>

      <main className="content-area">
        <Outlet />
      </main>
    </div>
  );
}

// ==========================================
// 3. TELAS (Páginas Falsas por enquanto)
// ==========================================
const Home = () => (
  <div className="content-area">
    <h1>Bem-vindo ao Sistema</h1>
    <p>Use o menu superior para navegar.</p>
  </div>
);

// ==========================================
// 4. CONFIGURAÇÃO DAS ROTAS (O Roteador)
// ==========================================
function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Tudo está dentro do MainLayout (Para manter a barra superior fixa) */}
        <Route path="/" element={<MainLayout />}>
          
          {/* Rota Inicial */}
          <Route index element={<Home />} />
          
          {/* Módulo de Produtos */}
          <Route path="produtos" element={<ProdutoLayout />}>
            <Route index element={<h2>Selecione uma opção no menu lateral</h2>} />
            <Route path="laboratorios" element={<Laboratorios />} />
            <Route path="principios-ativos" element={<PrincipiosAtivos/>} />
            <Route path="composicoes" element={<Composicoes/>} />
            <Route path="formulas" element={<Formulas/>} />
            <Route path="cadastro-produtos" element={<CadastroProduto/>} />
            <Route path="consulta-produtos" element={<ConsultaProdutos />} />
          </Route>

          {/* Módulos de Preços */}
          <Route path="precos" element={<Precos />} />

          {/* Módulo de Estoque */}
          <Route path="estoque" element={<EstoqueLayout />}>
            <Route index element={<h2>Selecione uma opção no menu lateral</h2>} />
            <Route path="entrada" element={<EntradaEstoque />} />
            <Route path="consulta" element={<ConsultaEstoque />} />
            <Route path="ajustes" element={<AjustesEstoque />} />
            <Route path="alertas" element={<AlertasEstoque />} />
            <Route path="movimentacoes" element={<MovimentacoesEstoque />} />
          </Route>

          {/* Módulo de Vendas */}
          <Route path="vendas" element={<VendaLayout />}>
            <Route index element={<h2>Selecione uma opção no menu lateral</h2>} />
            <Route path="caixa" element={<CaixaVenda />} />
            <Route path="historico" element={<HistoricoVendas />} />
            <Route path="resumo" element={<ResumoVendas />} />
          </Route>

        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;