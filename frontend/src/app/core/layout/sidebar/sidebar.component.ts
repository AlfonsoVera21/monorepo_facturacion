import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface NavItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  @Input() open = false;
  @Input() collapsed = false;
  @Output() closeSidebar = new EventEmitter<void>();
  @Output() toggleCollapsed = new EventEmitter<void>();

  protected readonly navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
    { label: 'Comprobantes', icon: 'description', route: '/comprobantes' },
    { label: 'Clientes', icon: 'group', route: '/clientes' },
    { label: 'Choferes', icon: 'badge', route: '/choferes' },
    { label: 'Productos', icon: 'inventory_2', route: '/productos' },
    { label: 'Inventario', icon: 'warehouse', route: '/inventario' },
    { label: 'Empresa', icon: 'business', route: '/empresa' },
    { label: 'Firma electronica', icon: 'verified_user', route: '/firma-electronica' },
    { label: 'SRI Monitor', icon: 'analytics', route: '/sri/monitor' },
    { label: 'Reportes', icon: 'assessment', route: '/reportes' },
    { label: 'Usuarios', icon: 'manage_accounts', route: '/usuarios' },
    { label: 'Configuracion', icon: 'settings', route: '/configuracion' }
  ];
}
